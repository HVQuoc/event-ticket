package com.daisy.tickets.services.impl;

import com.daisy.tickets.domain.dtos.CreatePaymentRequest;
import com.daisy.tickets.domain.dtos.PaymentResponse;
import com.daisy.tickets.domain.entities.Payment;
import com.daisy.tickets.domain.entities.PaymentStatusEnum;
import com.daisy.tickets.domain.entities.Ticket;
import com.daisy.tickets.domain.entities.User;
import com.daisy.tickets.exceptions.UserNotFoundException;
import com.daisy.tickets.repositories.UserRepository;
import com.daisy.tickets.domain.entities.TicketStatusEnum;
import com.daisy.tickets.domain.entities.TicketType;
import com.daisy.tickets.exceptions.PaymentException;
import com.daisy.tickets.exceptions.TicketTypeNotFoundException;
import com.daisy.tickets.repositories.PaymentRepository;
import com.daisy.tickets.repositories.TicketRepository;
import com.daisy.tickets.repositories.TicketTypeRepository;
import com.daisy.tickets.services.PaymentService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final TicketRepository ticketRepository;
  private final TicketTypeRepository ticketTypeRepository;
  private final UserRepository userRepository;

  @Value("${stripe.secret-key}")
  private String stripeSecretKey;

  @Value("${stripe.currency}")
  private String currency;

  @Transactional
  public PaymentResponse createPaymentSession(CreatePaymentRequest request) {

    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserNotFoundException(
            String.format("User with ID %s not found", request.getUserId())));

    // Validate ticket type exists and has available tickets
    TicketType ticketType = ticketTypeRepository.findByIdWithLock(request.getTicketTypeId())
        .orElseThrow(() -> new TicketTypeNotFoundException("Ticket type not found"));

    if (ticketType.getTotalAvailable() <= 0) {
      throw new PaymentException("No tickets available for this type");
    }

    // Decrease available tickets
    ticketType.setTotalAvailable(ticketType.getTotalAvailable() - 1);
    ticketTypeRepository.save(ticketType);

    // Create ticket with PENDING status
    Ticket ticket = Ticket.builder()
        .status(TicketStatusEnum.PENDING)
        .ticketType(ticketType)
        .purchaser(user)
        .build();
    ticket = ticketRepository.save(ticket);

    try {
      // Create Stripe checkout session
      SessionCreateParams params = SessionCreateParams.builder()
          .setMode(SessionCreateParams.Mode.PAYMENT)
          .setSuccessUrl(request.getSuccessUrl())
          .setCancelUrl(request.getCancelUrl())
          .addLineItem(SessionCreateParams.LineItem.builder()
              .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                  .setCurrency(currency)
                  .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                      .setName(ticketType.getName())
                      .setDescription(ticketType.getDescription())
                      .build())
                  .setUnitAmount(ticketType.getPrice().longValue() * 100) // Convert to cents
                  .build())
              .setQuantity(1L)
              .build())
          // .setCustomerEmail(request.getCustomerEmail())
          .putMetadata("ticket_id", ticket.getId().toString())
          .putMetadata("ticket_type_id", ticketType.getId().toString())
          .build();

      Session session = Session.create(params);

      // Create payment record
      Payment payment = Payment.builder()
          .stripeSessionId(session.getId())
          .amount(BigDecimal.valueOf(ticketType.getPrice()))
          .currency(currency)
          .status(PaymentStatusEnum.PENDING)
          .ticket(ticket)
          // .customerEmail(request.getCustomerEmail())
          .build();

      payment = paymentRepository.save(payment);

      log.info("Created payment session for ticket {} with Stripe session ID: {}",
          ticket.getId(), session.getId());

      return PaymentResponse.builder()
          .paymentId(payment.getId())
          .ticketId(ticket.getId())
          .stripeSessionId(session.getId())
          .checkoutUrl(session.getUrl())
          .amount(BigDecimal.valueOf(ticketType.getPrice()))
          .currency(currency)
          .status(payment.getStatus().name())
          // .customerEmail(request.getCustomerEmail())
          .build();

    } catch (Exception e) {
      log.error("Error creating Stripe session for ticket {}", ticket.getId(), e);
      // Update ticket status to FAILED
      ticket.setStatus(TicketStatusEnum.FAILED);
      ticketRepository.save(ticket);
      throw new PaymentException("Failed to create payment session", e);
    }
  }

  @Transactional
  public void handlePaymentSuccess(String sessionId) {
    Payment payment = paymentRepository.findByStripeSessionId(sessionId)
        .orElseThrow(() -> new PaymentException("Payment not found"));

    if (payment.getStatus() == PaymentStatusEnum.SUCCEEDED) {
      log.info("Payment already processed for session: {}", sessionId);
      return;
    }

    try {
      // Get session from Stripe to confirm payment
      //Stripe.apiKey = stripeSecretKey;
      Session session = Session.retrieve(sessionId);

      if ("complete".equals(session.getPaymentStatus())) {
        // Update payment status
        payment.setStatus(PaymentStatusEnum.SUCCEEDED);
        payment.setStripePaymentIntentId(session.getPaymentIntent());
        paymentRepository.save(payment);

        // Update ticket status
        Ticket ticket = payment.getTicket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticketRepository.save(ticket);

        log.info("Payment successful for ticket {} with session {}",
            ticket.getId(), sessionId);
      }
    } catch (Exception e) {
      log.error("Error processing successful payment for session: {}", sessionId, e);
      throw new PaymentException("Failed to process payment success", e);
    }
  }

  @Transactional
  public void handlePaymentFailure(String sessionId, String failureReason) {
    Payment payment = paymentRepository.findByStripeSessionId(sessionId)
        .orElseThrow(() -> new PaymentException("Payment not found"));

    if (payment.getStatus() == PaymentStatusEnum.FAILED) {
      log.info("Payment already marked as failed for session: {}", sessionId);
      return;
    }

    // Update payment status
    payment.setStatus(PaymentStatusEnum.FAILED);
    payment.setFailureReason(failureReason);
    paymentRepository.save(payment);

    // Update ticket status
    Ticket ticket = payment.getTicket();
    ticket.setStatus(TicketStatusEnum.FAILED);
    ticketRepository.save(ticket);

    log.info("Payment failed for ticket {} with session {}, reason: {}",
        ticket.getId(), sessionId, failureReason);
  }

  @Transactional
  public void handlePaymentFailureByPaymentIntent(String paymentIntentId, String failureReason) {
    Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
        .orElseGet(() -> {
          try {
            Stripe.apiKey = stripeSecretKey;
            // Try to locate a Checkout Session by this PaymentIntent ID
            com.stripe.param.checkout.SessionListParams listParams = com.stripe.param.checkout.SessionListParams
                .builder()
                .setPaymentIntent(paymentIntentId)
                .setLimit(1L)
                .build();
            com.stripe.model.checkout.SessionCollection sessions = com.stripe.model.checkout.Session.list(listParams);
            if (sessions != null && sessions.getData() != null && !sessions.getData().isEmpty()) {
              String sessionId = sessions.getData().get(0).getId();
              return paymentRepository.findByStripeSessionId(sessionId).orElse(null);
            }
          } catch (Exception e) {
            log.warn("Could not resolve Payment by paymentIntentId {} via Session.list: {}", paymentIntentId,
                e.getMessage());
          }
          return null;
        });

    if (payment == null) {
      log.warn("Payment not found by paymentIntentId: {}. Skipping state update.", paymentIntentId);
      return;
    }

    if (payment.getStatus() == PaymentStatusEnum.FAILED) {
      log.info("Payment already marked as failed for paymentIntent: {}", paymentIntentId);
      return;
    }

    // Update payment status and failure reason; ensure we persist the intent id too
    payment.setStatus(PaymentStatusEnum.FAILED);
    payment.setFailureReason(failureReason);
    if (payment.getStripePaymentIntentId() == null) {
      payment.setStripePaymentIntentId(paymentIntentId);
    }
    paymentRepository.save(payment);

    // Mark associated ticket as failed
    Ticket ticket = payment.getTicket();
    ticket.setStatus(TicketStatusEnum.FAILED);
    ticketRepository.save(ticket);

    log.info("Payment failed for ticket {} with paymentIntent {}, reason: {}",
        ticket.getId(), paymentIntentId, failureReason);
  }
}

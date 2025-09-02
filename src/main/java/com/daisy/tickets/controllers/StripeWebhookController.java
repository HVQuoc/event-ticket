package com.daisy.tickets.controllers;

import com.daisy.tickets.services.impl.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

  private final StripeService stripeService;

  @Value("${stripe.webhook-secret}")
  private String webhookSecret;

  @PostMapping
  public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
      @RequestHeader("Stripe-Signature") String sigHeader) {
    Event event;

    try {
      // Verify webhook signature
      event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
    } catch (SignatureVerificationException e) {
      log.error("Invalid webhook signature", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
    } catch (Exception e) {
      log.error("Error parsing webhook payload", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error parsing payload");
    }

    log.info("Received Stripe webhook event: {}", event.getType());

    try {
      switch (event.getType()) {
        case "checkout.session.completed":
          handleCheckoutSessionCompleted(event);
          break;
        case "checkout.session.expired":
          handleCheckoutSessionExpired(event);
          break;
        case "payment_intent.payment_failed":
          handlePaymentFailed(event);
          break;
        default:
          log.info("Unhandled event type: {}", event.getType());
      }

      return ResponseEntity.ok("Webhook processed successfully");
    } catch (Exception e) {
      log.error("Error processing webhook event: {}", event.getType(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
    }
  }

  private void handleCheckoutSessionCompleted(Event event) {
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject = null;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      log.error("Failed to deserialize checkout.session.completed event");
      return;
    }

    if (stripeObject instanceof Session) {
      Session session = (Session) stripeObject;
      log.info("Processing successful payment for session: {}", session.getId());
      stripeService.handlePaymentSuccess(session.getId());
    }
  }

  private void handleCheckoutSessionExpired(Event event) {
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject = null;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      log.error("Failed to deserialize checkout.session.expired event");
      return;
    }

    if (stripeObject instanceof Session) {
      Session session = (Session) stripeObject;
      log.info("Processing expired session: {}", session.getId());
      stripeService.handlePaymentFailure(session.getId(), "Session expired");
    }
  }

  private void handlePaymentFailed(Event event) {
    log.info("Processing payment failure event: {}", event.getId());

    // payment_intent.payment_failed carries a PaymentIntent object in data.object
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    if (dataObjectDeserializer.getObject().isEmpty()) {
      log.error("Failed to deserialize payment_intent.payment_failed event");
      return;
    }

    StripeObject stripeObject = dataObjectDeserializer.getObject().get();
    try {
      com.stripe.model.PaymentIntent paymentIntent = (com.stripe.model.PaymentIntent) stripeObject;
      String paymentIntentId = paymentIntent.getId();
      String failureReason = null;
      if (paymentIntent.getLastPaymentError() != null) {
        failureReason = paymentIntent.getLastPaymentError().getMessage();
      }
      if (failureReason == null) {
        failureReason = "Payment failed";
      }

      // Prefer updating by PaymentIntent ID since session may be unknown here
      stripeService.handlePaymentFailureByPaymentIntent(paymentIntentId, failureReason);
    } catch (ClassCastException e) {
      log.error("Unexpected object type in payment_intent.payment_failed: {}", stripeObject.getClass(), e);
    } catch (Exception e) {
      log.error("Error handling payment_intent.payment_failed", e);
    }
  }
}

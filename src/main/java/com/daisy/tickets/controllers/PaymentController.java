package com.daisy.tickets.controllers;

import com.daisy.tickets.domain.dtos.CreatePaymentRequest;
import com.daisy.tickets.domain.dtos.PaymentResponse;
import com.daisy.tickets.services.impl.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

  private final StripeService stripeService;

  // Create a payment session when user choose online payment method
  @PostMapping("/create-session")
  public ResponseEntity<PaymentResponse> createPaymentSession(@Valid @RequestBody CreatePaymentRequest request) {
    log.info("Creating payment session for ticket type: {}", request.getTicketTypeId());

    try {
      PaymentResponse response = stripeService.createPaymentSession(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error creating payment session", e);
      throw e;
    }
  }
}

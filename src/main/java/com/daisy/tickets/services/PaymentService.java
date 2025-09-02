package com.daisy.tickets.services;

import com.daisy.tickets.domain.dtos.CreatePaymentRequest;
import com.daisy.tickets.domain.dtos.PaymentResponse;

public interface PaymentService {

  // Create a payment session for ticket purchase
  PaymentResponse createPaymentSession(CreatePaymentRequest request);

  // Handle successful payment from webhook
  void handlePaymentSuccess(String sessionId);

  // Handle failed payment from webhook
  void handlePaymentFailure(String sessionId, String failureReason);

  // Handle failed payment by PaymentIntent ID (payment_intent.payment_failed)
  void handlePaymentFailureByPaymentIntent(String paymentIntentId, String failureReason);
}

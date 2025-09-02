package com.daisy.tickets.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private UUID paymentId;
  private UUID ticketId;
  private String stripeSessionId;
  private String checkoutUrl;
  private BigDecimal amount;
  private String currency;
  private String status;
  private String customerEmail;
}

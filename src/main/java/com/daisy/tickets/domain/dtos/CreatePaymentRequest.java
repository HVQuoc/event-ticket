package com.daisy.tickets.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

  @NotNull(message = "Ticket type ID is required")
  private UUID ticketTypeId;

  // @NotNull(message = "Customer email is required")
  // @Email(message = "Invalid email format")
  // private String customerEmail;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Success URL is required")
  private String successUrl;

  @NotNull(message = "Cancel URL is required")
  private String cancelUrl;
}

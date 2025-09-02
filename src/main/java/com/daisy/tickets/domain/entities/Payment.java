package com.daisy.tickets.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "stripe_session_id", unique = true)
  private String stripeSessionId;

  @Column(name = "stripe_payment_intent_id")
  private String stripePaymentIntentId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "currency", nullable = false)
  private String currency;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private PaymentStatusEnum status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private Ticket ticket;

  // @Column(name = "customer_email")
  // private String customerEmail;

  @Column(name = "failure_reason")
  private String failureReason;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    Payment payment = (Payment) o;
    return Objects.equals(id, payment.id) &&
        Objects.equals(stripeSessionId, payment.stripeSessionId) &&
        Objects.equals(createdAt, payment.createdAt) &&
        Objects.equals(updatedAt, payment.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, stripeSessionId, createdAt, updatedAt);
  }
}

package ru.mfa.carsharing.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Payment {
    private Long id;
    private Long rideId;
    private BigDecimal amount;
    private String currency = "RUB";
    private OffsetDateTime createdAt;
    private PaymentStatus status = PaymentStatus.PENDING;

    public Payment() {}

    public Payment(Long id, Long rideId, BigDecimal amount, String currency, OffsetDateTime createdAt, PaymentStatus status) {
        this.id = id;
        this.rideId = rideId;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}


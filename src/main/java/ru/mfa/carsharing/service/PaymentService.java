package ru.mfa.carsharing.service;

import org.springframework.stereotype.Service;
import ru.mfa.carsharing.exception.NotFoundException;
import ru.mfa.carsharing.model.Payment;
import ru.mfa.carsharing.model.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {
    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Payment> findAll() { return new ArrayList<>(payments.values()); }

    public Payment findById(Long id) {
        Payment p = payments.get(id);
        if (p == null) throw new NotFoundException("Payment not found: " + id);
        return p;
    }

    public Payment create(Payment p) {
        Long id = seq.incrementAndGet();
        p.setId(id);
        if (p.getCreatedAt() == null) p.setCreatedAt(OffsetDateTime.now());
        if (p.getAmount() == null) throw new IllegalArgumentException("amount is required");
        payments.put(id, p);
        return p;
    }

    public Payment update(Long id, Payment updated) {
        if (!payments.containsKey(id)) throw new NotFoundException("Payment not found: " + id);
        updated.setId(id);
        payments.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (payments.remove(id) == null) throw new NotFoundException("Payment not found: " + id);
    }

    public Payment createForRide(Long rideId, OffsetDateTime start, OffsetDateTime end, Double distanceKm) {
        if (start == null || end == null) throw new IllegalArgumentException("Ride must have start and end time");
        long minutes = Math.max(1, ChronoUnit.MINUTES.between(start, end));
        double km = distanceKm == null ? 0.0 : distanceKm;

        BigDecimal base = new BigDecimal("100");        // базовая стоимость
        BigDecimal perMinute = new BigDecimal("5");     // за минуту
        BigDecimal perKm = new BigDecimal("20");         // за км

        BigDecimal amount = base
                .add(perMinute.multiply(BigDecimal.valueOf(minutes)))
                .add(perKm.multiply(BigDecimal.valueOf(km)))
                .setScale(2, RoundingMode.HALF_UP);

        Payment p = new Payment();
        p.setRideId(rideId);
        p.setAmount(amount);
        p.setStatus(PaymentStatus.PENDING);
        p.setCreatedAt(OffsetDateTime.now());
        return create(p);
    }
}


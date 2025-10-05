package ru.mfa.carsharing.service;

import org.springframework.stereotype.Service;
import ru.mfa.carsharing.exception.NotFoundException;
import ru.mfa.carsharing.model.Car;
import ru.mfa.carsharing.model.Payment;
import ru.mfa.carsharing.model.Ride;
import ru.mfa.carsharing.model.RideStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RideService {
    private final Map<Long, Ride> rides = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private final UserService userService;
    private final CarService carService;
    private final PaymentService paymentService;

    public RideService(UserService userService, CarService carService, PaymentService paymentService) {
        this.userService = userService;
        this.carService = carService;
        this.paymentService = paymentService;
    }

    public List<Ride> findAll() { return new ArrayList<>(rides.values()); }

    public Ride findById(Long id) {
        Ride r = rides.get(id);
        if (r == null) throw new NotFoundException("Ride not found: " + id);
        return r;
    }

    public Ride create(Ride ride) {
        if (ride.getUserId() == null || ride.getCarId() == null) {
            throw new IllegalArgumentException("userId and carId are required");
        }
        // Validate references
        userService.findById(ride.getUserId());
        Car car = carService.findById(ride.getCarId());
        if (!car.isAvailable()) {
            throw new IllegalArgumentException("Car is not available: " + car.getId());
        }

        Long id = seq.incrementAndGet();
        ride.setId(id);
        ride.setStartTime(ride.getStartTime() == null ? OffsetDateTime.now() : ride.getStartTime());
        ride.setStatus(RideStatus.IN_PROGRESS);
        rides.put(id, ride);

        // Mark car as busy
        car.setAvailable(false);
        carService.update(car.getId(), car);
        return ride;
    }

    public Ride update(Long id, Ride updated) {
        if (!rides.containsKey(id)) throw new NotFoundException("Ride not found: " + id);
        updated.setId(id);
        rides.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        Ride r = rides.remove(id);
        if (r == null) throw new NotFoundException("Ride not found: " + id);
    }

    public Map<String, Object> finish(Long id, Double distanceKm) {
        Ride r = findById(id);
        if (r.getStatus() == RideStatus.FINISHED || r.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalArgumentException("Ride already finished or cancelled");
        }
        r.setEndTime(OffsetDateTime.now());
        r.setDistanceKm(distanceKm == null ? 0.0 : distanceKm);
        r.setStatus(RideStatus.FINISHED);
        rides.put(id, r);

        // free the car
        Car car = carService.findById(r.getCarId());
        car.setAvailable(true);
        carService.update(car.getId(), car);

        Payment payment = paymentService.createForRide(
        r.getId(), r.getStartTime(), r.getEndTime(), r.getDistanceKm());

        return Map.of("ride", r, "payment", payment);
    }
}


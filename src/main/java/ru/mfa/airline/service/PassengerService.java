package ru.mfa.airline.service;

import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PassengerService {
    private final Map<Long, Passenger> passengers = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Passenger> findAll() { return new ArrayList<>(passengers.values()); }

    public Passenger findById(Long id) {
        Passenger passenger = passengers.get(id);
        if (passenger == null) throw new NotFoundException("Passenger not found: " + id);
        return passenger;
    }

    public Passenger create(Passenger passenger) {
        Long id = seq.incrementAndGet();
        passenger.setId(id);
        if (passenger.getFirstName() == null || passenger.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
        if (passenger.getLastName() == null || passenger.getLastName().isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }
        passengers.put(id, passenger);
        return passenger;
    }

    public Passenger update(Long id, Passenger updated) {
        if (!passengers.containsKey(id)) throw new NotFoundException("Passenger not found: " + id);
        updated.setId(id);
        passengers.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (passengers.remove(id) == null) throw new NotFoundException("Passenger not found: " + id);
    }
}


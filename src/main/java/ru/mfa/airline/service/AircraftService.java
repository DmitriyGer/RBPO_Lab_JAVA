package ru.mfa.airline.service;

import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Aircraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AircraftService {
    private final Map<Long, Aircraft> aircrafts = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Aircraft> findAll() { return new ArrayList<>(aircrafts.values()); }

    public Aircraft findById(Long id) {
        Aircraft aircraft = aircrafts.get(id);
        if (aircraft == null) throw new NotFoundException("Aircraft not found: " + id);
        return aircraft;
    }

    public Aircraft create(Aircraft aircraft) {
        Long id = seq.incrementAndGet();
        aircraft.setId(id);
        if (aircraft.getRegistrationNumber() == null || aircraft.getRegistrationNumber().isBlank()) {
            throw new IllegalArgumentException("registrationNumber is required");
        }
        if (aircraft.getCapacity() == null || aircraft.getCapacity() <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        aircrafts.put(id, aircraft);
        return aircraft;
    }

    public Aircraft update(Long id, Aircraft updated) {
        if (!aircrafts.containsKey(id)) throw new NotFoundException("Aircraft not found: " + id);
        updated.setId(id);
        aircrafts.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (aircrafts.remove(id) == null) throw new NotFoundException("Aircraft not found: " + id);
    }
}


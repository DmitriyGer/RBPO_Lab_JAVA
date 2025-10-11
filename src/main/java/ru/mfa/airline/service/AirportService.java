package ru.mfa.airline.service;

import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Airport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AirportService {
    private final Map<Long, Airport> airports = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Airport> findAll() {
        return new ArrayList<>(airports.values());
    }

    public Airport findById(Long id) {
        Airport airport = airports.get(id);
        if (airport == null)
            throw new NotFoundException("Airport not found: " + id);
        return airport;
    }

    public Airport create(Airport airport) {
        Long id = seq.incrementAndGet();
        airport.setId(id);
        if (airport.getCode() == null || airport.getCode().isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        airports.put(id, airport);
        return airport;
    }

    public Airport update(Long id, Airport updated) {
        if (!airports.containsKey(id))
            throw new NotFoundException("Airport not found: " + id);
        updated.setId(id);
        airports.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (airports.remove(id) == null)
            throw new NotFoundException("Airport not found: " + id);
    }
}
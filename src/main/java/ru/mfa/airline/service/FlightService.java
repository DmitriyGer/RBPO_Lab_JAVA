package ru.mfa.airline.service;

import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FlightService {
    private final Map<Long, Flight> flights = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private final AircraftService aircraftService;
    private final AirportService airportService;

    public FlightService(AircraftService aircraftService, AirportService airportService) {
        this.aircraftService = aircraftService;
        this.airportService = airportService;
    }

    public List<Flight> findAll() {
        return new ArrayList<>(flights.values());
    }

    public Flight findById(Long id) {
        Flight flight = flights.get(id);
        if (flight == null)
            throw new NotFoundException("Flight not found: " + id);
        return flight;
    }

    public Flight create(Flight flight) {
        if (flight.getAircraftId() == null || flight.getDepartureAirportId() == null
                || flight.getArrivalAirportId() == null) {
            throw new IllegalArgumentException("aircraftId, departureAirportId and arrivalAirportId are required");
        }

        Aircraft aircraft = aircraftService.findById(flight.getAircraftId());
        if (!aircraft.isAvailable()) {
            throw new IllegalArgumentException("Aircraft is not available: " + aircraft.getId());
        }

        airportService.findById(flight.getDepartureAirportId());
        airportService.findById(flight.getArrivalAirportId());

        Long id = seq.incrementAndGet();
        flight.setId(id);
        flight.setStatus(FlightStatus.SCHEDULED);
        flights.put(id, flight);
        return flight;
    }

    public Flight update(Long id, Flight updated) {
        if (!flights.containsKey(id))
            throw new NotFoundException("Flight not found: " + id);
        updated.setId(id);
        flights.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        Flight flight = flights.remove(id);
        if (flight == null)
            throw new NotFoundException("Flight not found: " + id);
    }

    public Flight updateStatus(Long id, FlightStatus status) {
        Flight flight = findById(id);
        flight.setStatus(status);
        flights.put(id, flight);
        return flight;
    }
}

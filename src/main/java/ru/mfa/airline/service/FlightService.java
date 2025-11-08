package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;
import ru.mfa.airline.repository.FlightRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightService {
    public List<Flight> findAvailableFlightsByCity(String fromCity, String toCity) {
        return flightRepository.findAvailableFlightsByCity(fromCity, toCity, FlightStatus.SCHEDULED);
    }

    public List<Flight> findAvailableFlightsByCityAndDate(String fromCity, String toCity, String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            OffsetDateTime start = localDate.atStartOfDay(java.time.ZoneOffset.UTC).toOffsetDateTime();
            OffsetDateTime end = localDate.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toOffsetDateTime()
                    .minusNanos(1);
            return flightRepository.findAvailableFlightsByCityAndDate(fromCity, toCity, FlightStatus.SCHEDULED, start,
                    end);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format, expected yyyy-MM-dd");
        }
    }

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private AirportService airportService;

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + id));
    }

    @Transactional
    public Flight create(Flight flight) {
        if (flight.getAircraft() == null || flight.getDepartureAirport() == null
                || flight.getArrivalAirport() == null) {
            throw new IllegalArgumentException("aircraft, departureAirport and arrivalAirport are required");
        }

        Aircraft aircraft = aircraftService.findById(flight.getAircraft().getId());
        if (!aircraft.isAvailable()) {
            throw new IllegalArgumentException("Aircraft is not available: " + aircraft.getId());
        }

        Airport departureAirport = airportService.findById(flight.getDepartureAirport().getId());
        Airport arrivalAirport = airportService.findById(flight.getArrivalAirport().getId());

        flight.setAircraft(aircraft);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setStatus(FlightStatus.SCHEDULED);

        return flightRepository.save(flight);
    }

    public Flight update(Long id, Flight updated) {
        findById(id);
        updated.setId(id);
        return flightRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        flightRepository.deleteById(id);
    }

    @Transactional
    public Flight updateStatus(Long id, FlightStatus status) {
        Flight flight = findById(id);
        flight.setStatus(status);
        return flightRepository.save(flight);
    }

    public List<Flight> findByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status);
    }

    public List<Flight> findAvailableFlights(Long departureAirportId, Long arrivalAirportId) {
        return flightRepository.findAvailableFlights(departureAirportId, arrivalAirportId, FlightStatus.SCHEDULED);
    }

    public List<Flight> findFlightsByPeriod(OffsetDateTime start, OffsetDateTime end) {
        return flightRepository.findFlightsByDeparturePeriod(start, end);
    }
}

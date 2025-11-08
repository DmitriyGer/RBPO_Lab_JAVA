package ru.mfa.airline.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.dto.FlightCreateRequest;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;
import ru.mfa.airline.service.AircraftService;
import ru.mfa.airline.service.AirportService;
import ru.mfa.airline.service.FlightService;
import ru.mfa.airline.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private AirportService airportService;

    @GetMapping
    public List<Flight> all() {
        return flightService.findAll();
    }

    @GetMapping("/{id}")
    public Flight byId(@PathVariable Long id) {
        return flightService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Flight> create(@RequestBody FlightCreateRequest request) {
        Aircraft aircraft = aircraftService.findById(request.getAircraftId());
        Airport departureAirport = airportService.findById(request.getDepartureAirportId());
        Airport arrivalAirport = airportService.findById(request.getArrivalAirportId());

        Flight flight = new Flight();
        flight.setFlightNumber(request.getFlightNumber());
        flight.setAircraft(aircraft);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.create(flight));
    }

    @PutMapping("/{id}")
    public Flight update(@PathVariable Long id, @RequestBody Flight flight) {
        return flightService.update(id, flight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public Flight updateStatus(@PathVariable Long id, @RequestParam FlightStatus status) {
        return flightService.updateStatus(id, status);
    }

    @PostMapping("/{id}/cancel")
    public Flight cancelFlight(@PathVariable Long id) {
        Flight flight = flightService.updateStatus(id, FlightStatus.CANCELLED);
        bookingService.cancelAllBookingsForFlight(id);
        return flight;
    }

    @GetMapping("/search")
    public List<Flight> searchFlights(@RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String date) {
        if (from == null || to == null || from.isEmpty() || to.isEmpty()) {
            throw new IllegalArgumentException("Параметры from и to обязательны");
        }
        if (date != null && !date.isEmpty()) {
            return flightService.findAvailableFlightsByCityAndDate(from, to, date);
        } else {
            return flightService.findAvailableFlightsByCity(from, to);
        }
    }
}

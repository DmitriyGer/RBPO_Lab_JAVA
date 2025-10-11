package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;
import ru.mfa.airline.service.FlightService;
import ru.mfa.airline.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Flight> all() {
        return flightService.findAll();
    }

    @GetMapping("/{id}")
    public Flight byId(@PathVariable Long id) {
        return flightService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Flight> create(@RequestBody Flight flight) {
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
}

package ru.mfa.airline.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.dto.BookingCreateRequest;
import ru.mfa.airline.model.Booking;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.Passenger;
import ru.mfa.airline.service.BookingService;
import ru.mfa.airline.service.FlightService;
import ru.mfa.airline.service.PassengerService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerService passengerService;

    @GetMapping
    public List<Booking> all() {
        return bookingService.findAll();
    }

    @GetMapping("/{id}")
    public Booking byId(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody BookingCreateRequest request) {
        Flight flight = flightService.findById(request.getFlightId());
        Passenger passenger = passengerService.findById(request.getPassengerId());

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setPassenger(passenger);
        booking.setSeatNumber(request.getSeatNumber());
        booking.setPrice(request.getPrice());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(booking));
    }

    @PutMapping("/{id}")
    public Booking update(@PathVariable Long id, @RequestBody Booking booking) {
        return bookingService.update(id, booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cancel")
    public Booking cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }

    @GetMapping("/flight/{flightId}")
    public List<Booking> getBookingsByFlight(@PathVariable Long flightId) {
        return bookingService.findByFlightId(flightId);
    }

    @GetMapping("/passenger/{passengerId}")
    public List<Booking> getBookingsByPassenger(@PathVariable Long passengerId) {
        return bookingService.findByPassengerId(passengerId);
    }
}

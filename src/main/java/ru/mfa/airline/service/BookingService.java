package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Booking;
import ru.mfa.airline.model.BookingStatus;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;
import ru.mfa.airline.model.Passenger;
import ru.mfa.airline.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerService passengerService;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
    }

    @Transactional
    public Booking create(Booking booking) {
        if (booking.getFlight() == null || booking.getPassenger() == null) {
            throw new IllegalArgumentException("flight and passenger are required");
        }

        if (booking.getSeatNumber() == null || booking.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("seatNumber is required");
        }

        Flight flight = flightService.findById(booking.getFlight().getId());
        if (flight.getStatus() == FlightStatus.CANCELLED || flight.getStatus() == FlightStatus.DEPARTED) {
            throw new IllegalArgumentException("Cannot book flight with status: " + flight.getStatus());
        }

        Passenger passenger = passengerService.findById(booking.getPassenger().getId());

        if (bookingRepository.findByFlightIdAndSeatNumber(flight.getId(), booking.getSeatNumber()).isPresent()) {
            throw new IllegalArgumentException("Seat " + booking.getSeatNumber() + " is already taken on this flight");
        }

        long bookedSeats = bookingRepository.countConfirmedBookingsByFlight(flight.getId());
        if (bookedSeats >= flight.getAircraft().getCapacity()) {
            throw new IllegalArgumentException("Flight is fully booked");
        }

        if (!isValidSeatNumber(booking.getSeatNumber())) {
            throw new IllegalArgumentException("Invalid seat number format: " + booking.getSeatNumber());
        }

        booking.setFlight(flight);
        booking.setPassenger(passenger);
        booking.setBookingTime(OffsetDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        if (booking.getPrice() == null) {
            booking.setPrice(new BigDecimal("5000"));
        }

        return bookingRepository.save(booking);
    }

    private boolean isValidSeatNumber(String seatNumber) {
        return seatNumber.matches("\\d{1,2}[A-F]");
    }

    public Booking update(Long id, Booking updated) {
        findById(id);
        updated.setId(id);
        return bookingRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        bookingRepository.deleteById(id);
    }

    @Transactional
    public Booking cancel(Long id) {
        Booking booking = findById(id);

        if (booking.getFlight().getStatus() == FlightStatus.DEPARTED) {
            throw new IllegalArgumentException("Cannot cancel booking for departed flight");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelAllBookingsForFlight(Long flightId) {
        List<Booking> bookings = bookingRepository.findByFlightAndStatus(flightId, BookingStatus.CONFIRMED);
        bookings.forEach(booking -> {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        });
    }

    public List<Booking> findByFlightId(Long flightId) {
        return bookingRepository.findByFlightId(flightId);
    }

    public List<Booking> findByPassengerId(Long passengerId) {
        return bookingRepository.findByPassengerId(passengerId);
    }
}

package ru.mfa.airline.service;

import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Booking;
import ru.mfa.airline.model.BookingStatus;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    private final FlightService flightService;
    private final PassengerService passengerService;
    private final AircraftService aircraftService;

    public BookingService(FlightService flightService, PassengerService passengerService,
            AircraftService aircraftService) {
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.aircraftService = aircraftService;
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public Booking findById(Long id) {
        Booking booking = bookings.get(id);
        if (booking == null)
            throw new NotFoundException("Booking not found: " + id);
        return booking;
    }

    public Booking create(Booking booking) {
        if (booking.getFlightId() == null || booking.getPassengerId() == null) {
            throw new IllegalArgumentException("flightId and passengerId are required");
        }

        if (booking.getSeatNumber() == null || booking.getSeatNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("seatNumber is required");
        }

        Flight flight = flightService.findById(booking.getFlightId());
        if (flight.getStatus() == FlightStatus.CANCELLED || flight.getStatus() == FlightStatus.DEPARTED) {
            throw new IllegalArgumentException("Cannot book flight with status: " + flight.getStatus());
        }

        passengerService.findById(booking.getPassengerId());
        Aircraft aircraft = aircraftService.findById(flight.getAircraftId());

        // Проверка на дубликат места
        boolean seatTaken = bookings.values().stream()
                .anyMatch(b -> b.getFlightId().equals(booking.getFlightId())
                        && b.getSeatNumber().equals(booking.getSeatNumber())
                        && b.getStatus() == BookingStatus.CONFIRMED);

        if (seatTaken) {
            throw new IllegalArgumentException("Seat " + booking.getSeatNumber() + " is already taken on this flight");
        }

        // Проверка на превышение вместимости
        long bookedSeats = bookings.values().stream()
                .filter(b -> b.getFlightId().equals(booking.getFlightId()))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        if (bookedSeats >= aircraft.getCapacity()) {
            throw new IllegalArgumentException("Flight is fully booked");
        }

        // Валидация номера места (простая проверка формата)
        if (!isValidSeatNumber(booking.getSeatNumber())) {
            throw new IllegalArgumentException("Invalid seat number format: " + booking.getSeatNumber());
        }

        Long id = seq.incrementAndGet();
        booking.setId(id);
        booking.setBookingTime(OffsetDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        if (booking.getPrice() == null) {
            booking.setPrice(new BigDecimal("5000"));
        }

        bookings.put(id, booking);
        return booking;
    }

    private boolean isValidSeatNumber(String seatNumber) {
        // Простая валидация: число + буква (например, 12A, 5F, 33C)
        return seatNumber.matches("\\d{1,2}[A-F]");
    }

    public Booking update(Long id, Booking updated) {
        if (!bookings.containsKey(id))
            throw new NotFoundException("Booking not found: " + id);
        updated.setId(id);
        bookings.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (bookings.remove(id) == null)
            throw new NotFoundException("Booking not found: " + id);
    }

    public Booking cancel(Long id) {
        Booking booking = findById(id);
        Flight flight = flightService.findById(booking.getFlightId());

        if (flight.getStatus() == FlightStatus.DEPARTED) {
            throw new IllegalArgumentException("Cannot cancel booking for departed flight");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookings.put(id, booking);
        return booking;
    }

    public void cancelAllBookingsForFlight(Long flightId) {
        bookings.values().stream()
                .filter(booking -> booking.getFlightId().equals(flightId))
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookings.put(booking.getId(), booking);
                });
    }
}

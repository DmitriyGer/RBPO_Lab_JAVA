package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.*;
import ru.mfa.airline.repository.BookingRepository;
import ru.mfa.airline.repository.FlightRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AirlineBusinessService {

    @Autowired
    private FlightService flightService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Booking makeReservation(Long flightId, Long passengerId, String seatNumber, BigDecimal price) {
        Flight flight = flightService.findById(flightId);
        Passenger passenger = passengerService.findById(passengerId);

        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new IllegalArgumentException("Cannot make reservation for flight with status: " + flight.getStatus());
        }

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setPassenger(passenger);
        booking.setSeatNumber(seatNumber);
        booking.setPrice(price != null ? price : new BigDecimal("5000"));
        booking.setCurrency("RUB");

        return bookingService.create(booking);
    }

    @Transactional
    public void cancelFlight(Long flightId, String reason) {
        Flight flight = flightService.findById(flightId);

        if (flight.getStatus() == FlightStatus.DEPARTED) {
            throw new IllegalArgumentException("Cannot cancel departed flight");
        }

        flight.setStatus(FlightStatus.CANCELLED);
        flightRepository.save(flight);

        bookingService.cancelAllBookingsForFlight(flightId);
    }

    @Transactional
    public void delayFlight(Long flightId, OffsetDateTime newDepartureTime, OffsetDateTime newArrivalTime) {
        Flight flight = flightService.findById(flightId);

        if (flight.getStatus() == FlightStatus.DEPARTED || flight.getStatus() == FlightStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot delay flight with status: " + flight.getStatus());
        }

        flight.setStatus(FlightStatus.DELAYED);
        flight.setDepartureTime(newDepartureTime);
        flight.setArrivalTime(newArrivalTime);
        flightRepository.save(flight);
    }

    @Transactional
    public Flight checkInFlight(Long flightId) {
        Flight flight = flightService.findById(flightId);

        if (flight.getStatus() != FlightStatus.SCHEDULED && flight.getStatus() != FlightStatus.DELAYED) {
            throw new IllegalArgumentException("Cannot check in flight with status: " + flight.getStatus());
        }

        flight.setStatus(FlightStatus.BOARDING);
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight departFlight(Long flightId) {
        Flight flight = flightService.findById(flightId);

        if (flight.getStatus() != FlightStatus.BOARDING) {
            throw new IllegalArgumentException("Flight must be in boarding status to depart");
        }

        flight.setStatus(FlightStatus.DEPARTED);
        return flightRepository.save(flight);
    }

    public List<Booking> getFlightPassengerList(Long flightId) {
        flightService.findById(flightId);
        return bookingRepository.findByFlightAndStatus(flightId, BookingStatus.CONFIRMED);
    }

    public BigDecimal calculateFlightRevenue(Long flightId) {
        List<Booking> confirmedBookings = bookingRepository.findByFlightAndStatus(flightId, BookingStatus.CONFIRMED);
        return confirmedBookings.stream()
                .map(Booking::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public FlightOccupancyInfo getFlightOccupancy(Long flightId) {
        Flight flight = flightService.findById(flightId);
        long confirmedBookings = bookingRepository.countConfirmedBookingsByFlight(flightId);
        int capacity = flight.getAircraft().getCapacity();

        FlightOccupancyInfo info = new FlightOccupancyInfo();
        info.setFlightId(flightId);
        info.setCapacity(capacity);
        info.setOccupiedSeats((int) confirmedBookings);
        info.setAvailableSeats(capacity - (int) confirmedBookings);
        info.setOccupancyPercentage((double) confirmedBookings / capacity * 100);

        return info;
    }

    public static class FlightOccupancyInfo {
        private Long flightId;
        private int capacity;
        private int occupiedSeats;
        private int availableSeats;
        private double occupancyPercentage;

        public Long getFlightId() {
            return flightId;
        }

        public void setFlightId(Long flightId) {
            this.flightId = flightId;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getOccupiedSeats() {
            return occupiedSeats;
        }

        public void setOccupiedSeats(int occupiedSeats) {
            this.occupiedSeats = occupiedSeats;
        }

        public int getAvailableSeats() {
            return availableSeats;
        }

        public void setAvailableSeats(int availableSeats) {
            this.availableSeats = availableSeats;
        }

        public double getOccupancyPercentage() {
            return occupancyPercentage;
        }

        public void setOccupancyPercentage(double occupancyPercentage) {
            this.occupancyPercentage = occupancyPercentage;
        }
    }

    // Популярные направления
    public List<PopularRouteInfo> getPopularRoutes() {
        return bookingRepository.findPopularRoutes();
    }

    // Частые пассажиры
    public List<FrequentPassengerInfo> getFrequentPassengers() {
        return bookingRepository.findFrequentPassengers();
    }

    // Рейсы на сегодня
    public List<Flight> getTodaysFlights() {
        return flightRepository.findTodaysFlights();
    }

    // DTO классы для бизнес-операций
    public static class PopularRouteInfo {
        private String route;
        private String departureCity;
        private String arrivalCity;
        private Long bookingCount;

        public PopularRouteInfo(String departureCity, String arrivalCity, Long bookingCount) {
            this.departureCity = departureCity;
            this.arrivalCity = arrivalCity;
            this.route = departureCity + " → " + arrivalCity;
            this.bookingCount = bookingCount;
        }

        // Getters and setters
        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getDepartureCity() {
            return departureCity;
        }

        public void setDepartureCity(String departureCity) {
            this.departureCity = departureCity;
        }

        public String getArrivalCity() {
            return arrivalCity;
        }

        public void setArrivalCity(String arrivalCity) {
            this.arrivalCity = arrivalCity;
        }

        public Long getBookingCount() {
            return bookingCount;
        }

        public void setBookingCount(Long bookingCount) {
            this.bookingCount = bookingCount;
        }
    }

    public static class FrequentPassengerInfo {
        private Long passengerId;
        private String firstName;
        private String lastName;
        private String email;
        private Long bookingCount;

        public FrequentPassengerInfo(Long passengerId, String firstName, String lastName, String email,
                Long bookingCount) {
            this.passengerId = passengerId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.bookingCount = bookingCount;
        }

        // Getters and setters
        public Long getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(Long passengerId) {
            this.passengerId = passengerId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Long getBookingCount() {
            return bookingCount;
        }

        public void setBookingCount(Long bookingCount) {
            this.bookingCount = bookingCount;
        }
    }
}
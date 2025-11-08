package ru.mfa.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mfa.airline.model.Booking;
import ru.mfa.airline.model.BookingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId")
    List<Booking> findByFlightId(@Param("flightId") Long flightId);

    @Query("SELECT b FROM Booking b WHERE b.passenger.id = :passengerId")
    List<Booking> findByPassengerId(@Param("passengerId") Long passengerId);

    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId AND b.seatNumber = :seatNumber")
    Optional<Booking> findByFlightIdAndSeatNumber(@Param("flightId") Long flightId,
            @Param("seatNumber") String seatNumber);

    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId AND b.status = :status")
    List<Booking> findByFlightAndStatus(@Param("flightId") Long flightId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId AND b.status = 'CONFIRMED'")
    long countConfirmedBookingsByFlight(@Param("flightId") Long flightId);

    // Популярные направления (топ-5 маршрутов по количеству бронирований)
    @Query("SELECT new ru.mfa.airline.service.AirlineBusinessService$PopularRouteInfo(" +
            "f.departureAirport.city, f.arrivalAirport.city, COUNT(b.id)) " +
            "FROM Booking b JOIN b.flight f " +
            "WHERE b.status = 'CONFIRMED' " +
            "GROUP BY f.departureAirport.city, f.arrivalAirport.city " +
            "ORDER BY COUNT(b.id) DESC")
    List<ru.mfa.airline.service.AirlineBusinessService.PopularRouteInfo> findPopularRoutes();

    // Частые пассажиры (топ-5 пассажиров по количеству бронирований)
    @Query("SELECT new ru.mfa.airline.service.AirlineBusinessService$FrequentPassengerInfo(" +
            "p.id, p.firstName, p.lastName, p.email, COUNT(b.id)) " +
            "FROM Booking b JOIN b.passenger p " +
            "WHERE b.status = 'CONFIRMED' " +
            "GROUP BY p.id, p.firstName, p.lastName, p.email " +
            "ORDER BY COUNT(b.id) DESC")
    List<ru.mfa.airline.service.AirlineBusinessService.FrequentPassengerInfo> findFrequentPassengers();
}
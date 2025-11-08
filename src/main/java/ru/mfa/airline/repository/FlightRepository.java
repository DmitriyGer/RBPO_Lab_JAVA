package ru.mfa.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.model.FlightStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.city = :fromCity AND f.arrivalAirport.city = :toCity AND f.status = :status")
    List<Flight> findAvailableFlightsByCity(@Param("fromCity") String fromCity,
            @Param("toCity") String toCity,
            @Param("status") FlightStatus status);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.city = :fromCity AND f.arrivalAirport.city = :toCity AND f.status = :status AND f.departureTime >= :start AND f.departureTime <= :end")
    List<Flight> findAvailableFlightsByCityAndDate(@Param("fromCity") String fromCity,
            @Param("toCity") String toCity,
            @Param("status") FlightStatus status,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);

    Optional<Flight> findByFlightNumber(String flightNumber);

    List<Flight> findByStatus(FlightStatus status);

    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId")
    List<Flight> findByAircraftId(@Param("aircraftId") Long aircraftId);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.id = :departureAirportId")
    List<Flight> findByDepartureAirportId(@Param("departureAirportId") Long departureAirportId);

    @Query("SELECT f FROM Flight f WHERE f.arrivalAirport.id = :arrivalAirportId")
    List<Flight> findByArrivalAirportId(@Param("arrivalAirportId") Long arrivalAirportId);

    @Query("SELECT f FROM Flight f WHERE f.departureTime >= :start AND f.departureTime <= :end")
    List<Flight> findFlightsByDeparturePeriod(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.id = :depId AND f.arrivalAirport.id = :arrId AND f.status = :status")
    List<Flight> findAvailableFlights(@Param("depId") Long departureAirportId, @Param("arrId") Long arrivalAirportId,
            @Param("status") FlightStatus status);

    // Рейсы на сегодня
    @Query("SELECT f FROM Flight f WHERE CAST(f.departureTime AS date) = CURRENT_DATE ORDER BY f.departureTime")
    List<Flight> findTodaysFlights();
}
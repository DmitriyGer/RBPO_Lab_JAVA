package ru.mfa.airline.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.model.Booking;
import ru.mfa.airline.model.Flight;
import ru.mfa.airline.service.AirlineBusinessService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/airline")
public class AirlineBusinessController {

    @Autowired
    private AirlineBusinessService airlineBusinessService;

    @PostMapping("/reservation")
    public ResponseEntity<Booking> makeReservation(@RequestBody ReservationRequest request) {
        Booking booking = airlineBusinessService.makeReservation(
                request.getFlightId(),
                request.getPassengerId(),
                request.getSeatNumber(),
                request.getPrice());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/flights/{flightId}/cancel")
    public ResponseEntity<String> cancelFlight(@PathVariable Long flightId, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        airlineBusinessService.cancelFlight(flightId, reason);
        return ResponseEntity.ok("Flight cancelled successfully");
    }

    @PostMapping("/flights/{flightId}/delay")
    public ResponseEntity<String> delayFlight(@PathVariable Long flightId, @RequestBody DelayFlightRequest request) {
        airlineBusinessService.delayFlight(flightId, request.getNewDepartureTime(), request.getNewArrivalTime());
        return ResponseEntity.ok("Flight delayed successfully");
    }

    @PostMapping("/flights/{flightId}/check-in")
    public ResponseEntity<Flight> checkInFlight(@PathVariable Long flightId) {
        Flight flight = airlineBusinessService.checkInFlight(flightId);
        return ResponseEntity.ok(flight);
    }

    @PostMapping("/flights/{flightId}/depart")
    public ResponseEntity<Flight> departFlight(@PathVariable Long flightId) {
        Flight flight = airlineBusinessService.departFlight(flightId);
        return ResponseEntity.ok(flight);
    }

    @GetMapping("/flights/{flightId}/passengers")
    public ResponseEntity<List<Booking>> getFlightPassengers(@PathVariable Long flightId) {
        List<Booking> passengers = airlineBusinessService.getFlightPassengerList(flightId);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/flights/{flightId}/revenue")
    public ResponseEntity<Map<String, BigDecimal>> getFlightRevenue(@PathVariable Long flightId) {
        BigDecimal revenue = airlineBusinessService.calculateFlightRevenue(flightId);
        return ResponseEntity.ok(Map.of("revenue", revenue));
    }

    @GetMapping("/flights/{flightId}/occupancy")
    public ResponseEntity<AirlineBusinessService.FlightOccupancyInfo> getFlightOccupancy(@PathVariable Long flightId) {
        AirlineBusinessService.FlightOccupancyInfo occupancy = airlineBusinessService.getFlightOccupancy(flightId);
        return ResponseEntity.ok(occupancy);
    }

    @GetMapping("/popular-routes")
    public ResponseEntity<List<AirlineBusinessService.PopularRouteInfo>> getPopularRoutes() {
        List<AirlineBusinessService.PopularRouteInfo> routes = airlineBusinessService.getPopularRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/frequent-passengers")
    public ResponseEntity<List<AirlineBusinessService.FrequentPassengerInfo>> getFrequentPassengers() {
        List<AirlineBusinessService.FrequentPassengerInfo> passengers = airlineBusinessService.getFrequentPassengers();
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/todays-flights")
    public ResponseEntity<List<Flight>> getTodaysFlights() {
        List<Flight> flights = airlineBusinessService.getTodaysFlights();
        return ResponseEntity.ok(flights);
    }

    public static class ReservationRequest {
        private Long flightId;
        private Long passengerId;
        private String seatNumber;
        private BigDecimal price;

        public Long getFlightId() {
            return flightId;
        }

        public void setFlightId(Long flightId) {
            this.flightId = flightId;
        }

        public Long getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(Long passengerId) {
            this.passengerId = passengerId;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class DelayFlightRequest {
        private OffsetDateTime newDepartureTime;
        private OffsetDateTime newArrivalTime;

        public OffsetDateTime getNewDepartureTime() {
            return newDepartureTime;
        }

        public void setNewDepartureTime(OffsetDateTime newDepartureTime) {
            this.newDepartureTime = newDepartureTime;
        }

        public OffsetDateTime getNewArrivalTime() {
            return newArrivalTime;
        }

        public void setNewArrivalTime(OffsetDateTime newArrivalTime) {
            this.newArrivalTime = newArrivalTime;
        }
    }
}
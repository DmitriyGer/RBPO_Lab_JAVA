package ru.mfa.airline.model;

import java.time.OffsetDateTime;

public class Flight {
    private Long id;
    private String flightNumber;
    private Long aircraftId;
    private Long departureAirportId;
    private Long arrivalAirportId;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private FlightStatus status = FlightStatus.SCHEDULED;

    public Flight() {}

    public Flight(Long id, String flightNumber, Long aircraftId, Long departureAirportId, Long arrivalAirportId, 
                  OffsetDateTime departureTime, OffsetDateTime arrivalTime, FlightStatus status) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.aircraftId = aircraftId;
        this.departureAirportId = departureAirportId;
        this.arrivalAirportId = arrivalAirportId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public Long getAircraftId() { return aircraftId; }
    public void setAircraftId(Long aircraftId) { this.aircraftId = aircraftId; }
    public Long getDepartureAirportId() { return departureAirportId; }
    public void setDepartureAirportId(Long departureAirportId) { this.departureAirportId = departureAirportId; }
    public Long getArrivalAirportId() { return arrivalAirportId; }
    public void setArrivalAirportId(Long arrivalAirportId) { this.arrivalAirportId = arrivalAirportId; }
    public OffsetDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(OffsetDateTime departureTime) { this.departureTime = departureTime; }
    public OffsetDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(OffsetDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
}


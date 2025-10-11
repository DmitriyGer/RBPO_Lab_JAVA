package ru.mfa.airline.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Booking {
    private Long id;
    private Long flightId;
    private Long passengerId;
    private String seatNumber;
    private BigDecimal price;
    private String currency = "RUB";
    private OffsetDateTime bookingTime;
    private BookingStatus status = BookingStatus.CONFIRMED;

    public Booking() {}

    public Booking(Long id, Long flightId, Long passengerId, String seatNumber, BigDecimal price, 
                   String currency, OffsetDateTime bookingTime, BookingStatus status) {
        this.id = id;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.currency = currency;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public OffsetDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(OffsetDateTime bookingTime) { this.bookingTime = bookingTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}


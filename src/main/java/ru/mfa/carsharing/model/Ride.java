package ru.mfa.carsharing.model;

import java.time.OffsetDateTime;

public class Ride {
    private Long id;
    private Long userId;
    private Long carId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double distanceKm;
    private RideStatus status = RideStatus.CREATED;

    public Ride() {}

    public Ride(Long id, Long userId, Long carId, OffsetDateTime startTime, OffsetDateTime endTime, Double distanceKm, RideStatus status) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distanceKm = distanceKm;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
}


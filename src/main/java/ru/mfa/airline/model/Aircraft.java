package ru.mfa.airline.model;

public class Aircraft {
    private Long id;
    private String model;
    private String manufacturer;
    private String registrationNumber;
    private Integer capacity;
    private boolean available = true;

    public Aircraft() {}

    public Aircraft(Long id, String model, String manufacturer, String registrationNumber, Integer capacity, boolean available) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.available = available;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}


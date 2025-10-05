package ru.mfa.carsharing.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private String drivingLicense;

    public User() {}

    public User(Long id, String name, String email, String drivingLicense) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.drivingLicense = drivingLicense;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDrivingLicense() { return drivingLicense; }
    public void setDrivingLicense(String drivingLicense) { this.drivingLicense = drivingLicense; }
}


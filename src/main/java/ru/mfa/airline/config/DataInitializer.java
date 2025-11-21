package ru.mfa.airline.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.mfa.airline.model.*;
import ru.mfa.airline.service.AircraftService;
import ru.mfa.airline.service.AirportService;
import ru.mfa.airline.service.FlightService;
import ru.mfa.airline.service.PassengerService;

import java.time.OffsetDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private AirportService airportService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private FlightService flightService;

    @Override
    public void run(String... args) throws Exception {
        if (aircraftService.findAll().isEmpty()) {
            initializeData();
        }
    }

    private void initializeData() {
        Aircraft aircraft1 = new Aircraft();
        aircraft1.setModel("Boeing 737");
        aircraft1.setManufacturer("Boeing");
        aircraft1.setRegistrationNumber("RA-73001");
        aircraft1.setCapacity(189);
        aircraft1.setAvailable(true);
        aircraft1 = aircraftService.create(aircraft1);

        Aircraft aircraft2 = new Aircraft();
        aircraft2.setModel("Airbus A320");
        aircraft2.setManufacturer("Airbus");
        aircraft2.setRegistrationNumber("RA-32001");
        aircraft2.setCapacity(180);
        aircraft2.setAvailable(true);
        aircraft2 = aircraftService.create(aircraft2);

        Airport airport1 = new Airport();
        airport1.setCode("SVO");
        airport1.setName("Sheremetyevo International Airport");
        airport1.setCity("Moscow");
        airport1.setCountry("Russia");
        airport1 = airportService.create(airport1);

        Airport airport2 = new Airport();
        airport2.setCode("LED");
        airport2.setName("Pulkovo Airport");
        airport2.setCity("Saint Petersburg");
        airport2.setCountry("Russia");
        airport2 = airportService.create(airport2);

        Airport airport3 = new Airport();
        airport3.setCode("KZN");
        airport3.setName("Kazan International Airport");
        airport3.setCity("Kazan");
        airport3.setCountry("Russia");
        airport3 = airportService.create(airport3);

        Passenger passenger1 = new Passenger();
        passenger1.setFirstName("Иван");
        passenger1.setLastName("Иванов");
        passenger1.setEmail("ivan@example.com");
        passenger1.setPassportNumber("1234567890");
        passenger1.setPhoneNumber("+79001234567");
        passenger1 = passengerService.create(passenger1);

        Passenger passenger2 = new Passenger();
        passenger2.setFirstName("Мария");
        passenger2.setLastName("Петрова");
        passenger2.setEmail("maria@example.com");
        passenger2.setPassportNumber("0987654321");
        passenger2.setPhoneNumber("+79007654321");
        passenger2 = passengerService.create(passenger2);

        Flight flight1 = new Flight();
        flight1.setFlightNumber("SU100");
        flight1.setAircraft(aircraft1);
        flight1.setDepartureAirport(airport1);
        flight1.setArrivalAirport(airport2);
        flight1.setDepartureTime(OffsetDateTime.now().plusDays(1));
        flight1.setArrivalTime(OffsetDateTime.now().plusDays(1).plusHours(2));
        flight1 = flightService.create(flight1);

        Flight flight2 = new Flight();
        flight2.setFlightNumber("SU200");
        flight2.setAircraft(aircraft2);
        flight2.setDepartureAirport(airport2);
        flight2.setArrivalAirport(airport3);
        flight2.setDepartureTime(OffsetDateTime.now().plusDays(2));
        flight2.setArrivalTime(OffsetDateTime.now().plusDays(2).plusHours(1));
        flight2 = flightService.create(flight2);

        System.out.println("Test data initialized successfully!");
        System.out.println("Aircrafts: " + aircraftService.findAll().size());
        System.out.println("Airports: " + airportService.findAll().size());
        System.out.println("Passengers: " + passengerService.findAll().size());
        System.out.println("Flights: " + flightService.findAll().size());
    }
}
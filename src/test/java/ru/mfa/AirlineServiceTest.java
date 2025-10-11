package ru.mfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Passenger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AirlineServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void helloEndpointReturnsHelloWorld() {
        ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/api/hello", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo("Hello, World!");
    }

    @Test
    void airportCrudOperations() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Airport airport = new Airport();
        airport.setCode("SVO");
        airport.setName("Sheremetyevo");
        airport.setCity("Moscow");
        airport.setCountry("Russia");

        HttpEntity<Airport> request = new HttpEntity<>(airport, headers);
        ResponseEntity<Airport> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/airports", request, Airport.class);
        
        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getCode()).isEqualTo("SVO");

        ResponseEntity<Airport[]> getAllResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/airports", Airport[].class);
        assertThat(getAllResponse.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    void aircraftCrudOperations() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Aircraft aircraft = new Aircraft();
        aircraft.setModel("Boeing 737");
        aircraft.setManufacturer("Boeing");
        aircraft.setRegistrationNumber("RA-73001");
        aircraft.setCapacity(180);
        aircraft.setAvailable(true);

        HttpEntity<Aircraft> request = new HttpEntity<>(aircraft, headers);
        ResponseEntity<Aircraft> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/aircrafts", request, Aircraft.class);
        
        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getCapacity()).isEqualTo(180);
    }

    @Test
    void passengerCrudOperations() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Passenger passenger = new Passenger();
        passenger.setFirstName("Ivan");
        passenger.setLastName("Petrov");
        passenger.setEmail("ivan.petrov@email.com");
        passenger.setPassportNumber("1234567890");
        passenger.setPhoneNumber("+71234567890");

        HttpEntity<Passenger> request = new HttpEntity<>(passenger, headers);
        ResponseEntity<Passenger> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/passengers", request, Passenger.class);
        
        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getFirstName()).isEqualTo("Ivan");
    }
}


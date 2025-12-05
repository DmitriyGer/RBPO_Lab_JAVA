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
import ru.mfa.airline.dto.TokenPairResponse;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.model.Passenger;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AirlineServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> registerPayload = Map.of(
                "username", "admin",
                "password", "Admin@1234",
                "role", "ADMIN");
        restTemplate.postForEntity(baseUrl() + "/api/auth/register", registerPayload, Object.class);

        Map<String, Object> loginPayload = Map.of(
                "username", "admin",
                "password", "Admin@1234");
        ResponseEntity<TokenPairResponse> loginResp = restTemplate.postForEntity(
                baseUrl() + "/api/auth/login", loginPayload, TokenPairResponse.class);

        assertThat(loginResp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(loginResp.getBody()).isNotNull();

        headers.setBearerAuth(loginResp.getBody().getAccessToken());
        return headers;
    }

    @Test
    void helloEndpointReturnsHelloWorld() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl() + "/api/hello", HttpMethod.GET, request,
                String.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo("Hello, World!");
    }

    @Test
    void airportCrudOperations() {
        HttpHeaders headers = authHeaders();

        Airport airport = new Airport();
        String airportCode = "T" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();
        airport.setCode(airportCode);
        airport.setName("Test Hub");
        airport.setCity("TestCity");
        airport.setCountry("TestLand");

        HttpEntity<Airport> request = new HttpEntity<>(airport, headers);
        ResponseEntity<Airport> createResponse = restTemplate.postForEntity(
                baseUrl() + "/api/airports", request, Airport.class);

        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getCode()).isEqualTo(airportCode);

        HttpEntity<Void> getAllRequest = new HttpEntity<>(headers);
        ResponseEntity<Airport[]> getAllResponse = restTemplate.exchange(
                baseUrl() + "/api/airports", HttpMethod.GET, getAllRequest, Airport[].class);
        assertThat(getAllResponse.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    void aircraftCrudOperations() {
        HttpHeaders headers = authHeaders();

        Aircraft aircraft = new Aircraft();
        aircraft.setModel("Boeing 737");
        aircraft.setManufacturer("Boeing");
        aircraft.setRegistrationNumber("RA-" + UUID.randomUUID().toString().substring(0, 5));
        aircraft.setCapacity(180);
        aircraft.setAvailable(true);

        HttpEntity<Aircraft> request = new HttpEntity<>(aircraft, headers);
        ResponseEntity<Aircraft> createResponse = restTemplate.postForEntity(
                baseUrl() + "/api/aircrafts", request, Aircraft.class);

        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getCapacity()).isEqualTo(180);
    }

    @Test
    void passengerCrudOperations() {
        HttpHeaders headers = authHeaders();

        Passenger passenger = new Passenger();
        passenger.setFirstName("Ivan");
        passenger.setLastName("Petrov");
        passenger.setEmail("ivan." + UUID.randomUUID().toString().substring(0, 4) + "@email.com");
        passenger.setPassportNumber(UUID.randomUUID().toString().substring(0, 10));
        passenger.setPhoneNumber("+7" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));

        HttpEntity<Passenger> request = new HttpEntity<>(passenger, headers);
        ResponseEntity<Passenger> createResponse = restTemplate.postForEntity(
                baseUrl() + "/api/passengers", request, Passenger.class);

        assertThat(createResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getFirstName()).isEqualTo("Ivan");
    }
}

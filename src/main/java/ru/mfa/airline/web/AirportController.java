package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.service.AirportService;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {
    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public List<Airport> all() {
        return airportService.findAll();
    }

    @GetMapping("/{id}")
    public Airport byId(@PathVariable Long id) {
        return airportService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody Airport airport) {
        return ResponseEntity.status(HttpStatus.CREATED).body(airportService.create(airport));
    }

    @PutMapping("/{id}")
    public Airport update(@PathVariable Long id, @RequestBody Airport airport) {
        return airportService.update(id, airport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        airportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
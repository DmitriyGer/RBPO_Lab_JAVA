package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.service.AircraftService;

import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
public class AircraftController {
    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public List<Aircraft> all() { return aircraftService.findAll(); }

    @GetMapping("/{id}")
    public Aircraft byId(@PathVariable Long id) { return aircraftService.findById(id); }

    @PostMapping
    public ResponseEntity<Aircraft> create(@RequestBody Aircraft aircraft) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aircraftService.create(aircraft));
    }

    @PutMapping("/{id}")
    public Aircraft update(@PathVariable Long id, @RequestBody Aircraft aircraft) { 
        return aircraftService.update(id, aircraft); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        aircraftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.model.Passenger;
import ru.mfa.airline.service.PassengerService;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public List<Passenger> all() { return passengerService.findAll(); }

    @GetMapping("/{id}")
    public Passenger byId(@PathVariable Long id) { return passengerService.findById(id); }

    @PostMapping
    public ResponseEntity<Passenger> create(@RequestBody Passenger passenger) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.create(passenger));
    }

    @PutMapping("/{id}")
    public Passenger update(@PathVariable Long id, @RequestBody Passenger passenger) { 
        return passengerService.update(id, passenger); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


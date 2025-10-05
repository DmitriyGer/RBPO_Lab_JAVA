package ru.mfa.carsharing.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.carsharing.model.Ride;
import ru.mfa.carsharing.service.RideService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping
    public List<Ride> all() { return rideService.findAll(); }

    @GetMapping("/{id}")
    public Ride byId(@PathVariable Long id) { return rideService.findById(id); }

    @PostMapping
    public ResponseEntity<Ride> create(@RequestBody Ride ride) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.create(ride));
    }

    @PutMapping("/{id}")
    public Ride update(@PathVariable Long id, @RequestBody Ride ride) { return rideService.update(id, ride); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rideService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Завершение поездки и автоматическое создание платежа
    @PostMapping("/{id}/finish")
    public Map<String, Object> finish(@PathVariable Long id, @RequestParam(required = false) Double distanceKm) {
        return rideService.finish(id, distanceKm);
    }
}


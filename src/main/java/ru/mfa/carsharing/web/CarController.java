package ru.mfa.carsharing.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.carsharing.model.Car;
import ru.mfa.carsharing.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<Car> all() { return carService.findAll(); }

    @GetMapping("/{id}")
    public Car byId(@PathVariable Long id) { return carService.findById(id); }

    @PostMapping
    public ResponseEntity<Car> create(@RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(car));
    }

    @PutMapping("/{id}")
    public Car update(@PathVariable Long id, @RequestBody Car car) { return carService.update(id, car); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


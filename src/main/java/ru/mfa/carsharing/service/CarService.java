package ru.mfa.carsharing.service;

import org.springframework.stereotype.Service;
import ru.mfa.carsharing.exception.NotFoundException;
import ru.mfa.carsharing.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarService {
    private final Map<Long, Car> cars = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Car> findAll() { return new ArrayList<>(cars.values()); }

    public Car findById(Long id) {
        Car car = cars.get(id);
        if (car == null) throw new NotFoundException("Car not found: " + id);
        return car;
    }

    public Car create(Car car) {
        Long id = seq.incrementAndGet();
        car.setId(id);
        if (car.getLicensePlate() == null || car.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("licensePlate is required");
        }
        cars.put(id, car);
        return car;
    }

    public Car update(Long id, Car updated) {
        if (!cars.containsKey(id)) throw new NotFoundException("Car not found: " + id);
        updated.setId(id);
        cars.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (cars.remove(id) == null) throw new NotFoundException("Car not found: " + id);
    }
}


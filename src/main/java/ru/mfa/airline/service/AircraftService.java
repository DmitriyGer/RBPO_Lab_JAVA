package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Aircraft;
import ru.mfa.airline.repository.AircraftRepository;

import java.util.List;

/**
 * Сервис для работы с самолетами
 */
@Service
public class AircraftService {
    @Autowired
    private AircraftRepository aircraftRepository;

    public List<Aircraft> findAll() {
        return aircraftRepository.findAll();
    }

    public Aircraft findById(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aircraft not found: " + id));
    }

    public Aircraft create(Aircraft aircraft) {
        if (aircraft.getRegistrationNumber() == null || aircraft.getRegistrationNumber().isBlank()) {
            throw new IllegalArgumentException("registrationNumber is required");
        }
        if (aircraft.getCapacity() == null || aircraft.getCapacity() <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (aircraftRepository.findByRegistrationNumber(aircraft.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException(
                    "Aircraft with registration number already exists: " + aircraft.getRegistrationNumber());
        }

        return aircraftRepository.save(aircraft);
    }

    public Aircraft update(Long id, Aircraft updated) {
        findById(id);

        updated.setId(id);
        return aircraftRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        aircraftRepository.deleteById(id);
    }

    public List<Aircraft> findAvailableAircrafts() {
        return aircraftRepository.findByAvailableTrue();
    }
}

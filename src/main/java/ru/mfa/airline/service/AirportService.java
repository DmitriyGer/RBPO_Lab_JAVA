package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Airport;
import ru.mfa.airline.repository.AirportRepository;

import java.util.List;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Airport findById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Airport not found: " + id));
    }

    public Airport create(Airport airport) {
        if (airport.getCode() == null || airport.getCode().isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (airportRepository.findByCode(airport.getCode()).isPresent()) {
            throw new IllegalArgumentException("Airport with code already exists: " + airport.getCode());
        }
        return airportRepository.save(airport);
    }

    public Airport update(Long id, Airport updated) {
        findById(id);
        updated.setId(id);
        return airportRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        airportRepository.deleteById(id);
    }

    public List<Airport> findByCity(String city) {
        return airportRepository.findByCity(city);
    }

    public List<Airport> findByCountry(String country) {
        return airportRepository.findByCountry(country);
    }
}
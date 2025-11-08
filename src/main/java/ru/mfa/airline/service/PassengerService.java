package ru.mfa.airline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfa.airline.exception.NotFoundException;
import ru.mfa.airline.model.Passenger;
import ru.mfa.airline.repository.PassengerRepository;

import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public List<Passenger> findAll() {
        return passengerRepository.findAll();
    }

    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Passenger not found: " + id));
    }

    public Passenger create(Passenger passenger) {
        if (passenger.getFirstName() == null || passenger.getFirstName().isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }
        if (passenger.getLastName() == null || passenger.getLastName().isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }
        if (passenger.getEmail() != null && passengerRepository.findByEmail(passenger.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Passenger with email already exists: " + passenger.getEmail());
        }
        if (passenger.getPassportNumber() != null
                && passengerRepository.findByPassportNumber(passenger.getPassportNumber()).isPresent()) {
            throw new IllegalArgumentException(
                    "Passenger with passport number already exists: " + passenger.getPassportNumber());
        }
        return passengerRepository.save(passenger);
    }

    public Passenger update(Long id, Passenger updated) {
        findById(id);
        updated.setId(id);
        return passengerRepository.save(updated);
    }

    public void delete(Long id) {
        findById(id);
        passengerRepository.deleteById(id);
    }
}

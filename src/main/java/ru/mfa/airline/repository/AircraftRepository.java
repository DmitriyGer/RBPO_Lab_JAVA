package ru.mfa.airline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mfa.airline.model.Aircraft;

import java.util.List;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);

    List<Aircraft> findByAvailableTrue();

    List<Aircraft> findByManufacturer(String manufacturer);
}
package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationRepository {
    Medication save(Medication medication);
    Optional<Medication> findById(UUID id);
    Optional<Medication> findByName(String name);
    List<Medication> searchByName(String query);
    List<Medication> findAll();
}

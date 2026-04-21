package com.utp.backwebintegrado.patient.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    List<Patient> findAll();
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(UUID userId);
    void deleteById(UUID id);

}

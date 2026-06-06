package com.utp.backwebintegrado.patient.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    List<Patient> findAll();
    Page<Patient> findAll(UUID userId, String query, String status, Pageable pageable);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(UUID userId);
    Optional<Patient> findByUserId(UUID userId);
    void deleteById(UUID id);

}

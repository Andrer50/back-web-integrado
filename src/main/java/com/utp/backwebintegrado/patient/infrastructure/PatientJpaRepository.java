package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientJpaRepository extends JpaRepository<Patient, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(UUID userId);
}
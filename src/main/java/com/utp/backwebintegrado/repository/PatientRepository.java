package com.utp.backwebintegrado.repository;

import com.utp.backwebintegrado.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
}

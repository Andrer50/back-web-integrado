package com.utp.backwebintegrado.doctor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(UUID id);
    Page<Doctor> findAll(String query, Pageable pageable);
    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);
    Optional<Doctor> findByUserEmail(String email);
}

package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepository {

    // Inyecta el JpaRepository, no el Port
    private final PatientJpaRepository jpaRepository;

    @Override
    public Patient save(Patient patient) {
        return jpaRepository.save(patient);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Patient> findAll(UUID userId, String query, String status, Pageable pageable) {
        return jpaRepository.searchPatients(userId, query, status, pageable);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public Optional<Patient> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

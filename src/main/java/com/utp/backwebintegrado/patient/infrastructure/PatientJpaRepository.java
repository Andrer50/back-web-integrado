package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PatientJpaRepository extends JpaRepository<Patient, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(UUID userId);
    Optional<Patient> findByUserId(UUID userId);
    Optional<Patient> findByUserEmail(String email);

    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "u.role = 'PATIENT' AND " +
           "(:userId IS NULL OR u.id = :userId) AND " +
           "(:query IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR " +
           "p.documentNumber LIKE CONCAT('%', CAST(:query AS string), '%') OR " +
           "LOWER(u.email) = LOWER(CAST(:query AS string))) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<Patient> searchPatients(
            @Param("userId") UUID userId,
            @Param("query") String query,
            @Param("status") String status,
            Pageable pageable
    );
}
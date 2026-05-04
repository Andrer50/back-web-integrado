package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PatientJpaRepository extends JpaRepository<Patient, UUID> {
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByUserId(UUID userId);

    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "(:query IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.documentNumber LIKE CONCAT('%', :query, '%')) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<Patient> searchPatients(@Param("query") String query, @Param("status") String status, Pageable pageable);
}
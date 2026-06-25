package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DoctorJpaRepository extends JpaRepository<Doctor, UUID> {
    
    @Query("SELECT d FROM Doctor d JOIN d.user u " +
           "WHERE (:query IS NULL OR " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR " +
           "LOWER(d.medicalLicenseNumber) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))")
    Page<Doctor> searchDoctors(@Param("query") String query, Pageable pageable);

    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE u.email = :email")
    Optional<Doctor> findByUserEmail(@Param("email") String email);
}

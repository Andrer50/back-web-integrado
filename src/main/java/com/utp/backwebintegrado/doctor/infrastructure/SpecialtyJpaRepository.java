package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.Specialty;
import com.utp.backwebintegrado.shared.enumeration.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpecialtyJpaRepository extends JpaRepository<Specialty, UUID> {
    
    @Query("SELECT s FROM Specialty s WHERE " +
           "(:query IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))) AND " +
           "(:status IS NULL OR s.status = :status)")
    Page<Specialty> searchSpecialties(@Param("query") String query, 
                                     @Param("status") Status status, 
                                     Pageable pageable);

    boolean existsByName(String name);
}

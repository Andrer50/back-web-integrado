package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, UUID> {
    Optional<Diagnosis> findByIcd10(String icd10);

    @Query("SELECT d FROM Diagnosis d WHERE LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.icd10) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Diagnosis> searchByDescription(@Param("query") String query);
}

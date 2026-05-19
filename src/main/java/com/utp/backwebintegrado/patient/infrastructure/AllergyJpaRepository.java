package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AllergyJpaRepository extends JpaRepository<Allergy, UUID> {
    List<Allergy> findByPatient_Id(UUID patientId);
}

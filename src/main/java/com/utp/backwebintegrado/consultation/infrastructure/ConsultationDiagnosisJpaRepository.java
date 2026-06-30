package com.utp.backwebintegrado.consultation.infrastructure;

import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultationDiagnosisJpaRepository extends JpaRepository<ConsultationDiagnosis, UUID> {
    List<ConsultationDiagnosis> findByConsultation_Id(UUID consultationId);
}

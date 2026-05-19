package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.ConsultationVitals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultationVitalsJpaRepository extends JpaRepository<ConsultationVitals, UUID> {
    List<ConsultationVitals> findByConsultation_Id(UUID consultationId);
}

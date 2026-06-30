package com.utp.backwebintegrado.consultation.infrastructure;

import com.utp.backwebintegrado.consultation.domain.ConsultationVitals;
import com.utp.backwebintegrado.consultation.domain.ConsultationVitalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultationVitalsRepositoryImpl implements ConsultationVitalsRepository {

    private final ConsultationVitalsJpaRepository jpaRepository;

    @Override
    public ConsultationVitals save(ConsultationVitals vitals) {
        return jpaRepository.save(vitals);
    }

    @Override
    public List<ConsultationVitals> findByConsultationId(UUID consultationId) {
        return jpaRepository.findByConsultation_Id(consultationId);
    }
}

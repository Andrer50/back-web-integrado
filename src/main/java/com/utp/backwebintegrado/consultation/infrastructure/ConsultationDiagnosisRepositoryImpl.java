package com.utp.backwebintegrado.consultation.infrastructure;

import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosis;
import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultationDiagnosisRepositoryImpl implements ConsultationDiagnosisRepository {

    private final ConsultationDiagnosisJpaRepository jpaRepository;

    @Override
    public ConsultationDiagnosis save(ConsultationDiagnosis diagnosis) {
        return jpaRepository.save(diagnosis);
    }

    @Override
    public List<ConsultationDiagnosis> findByConsultationId(UUID consultationId) {
        return jpaRepository.findByConsultation_Id(consultationId);
    }
}

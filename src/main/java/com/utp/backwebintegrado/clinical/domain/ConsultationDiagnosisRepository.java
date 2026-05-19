package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.UUID;

public interface ConsultationDiagnosisRepository {
    ConsultationDiagnosis save(ConsultationDiagnosis diagnosis);
    List<ConsultationDiagnosis> findByConsultationId(UUID consultationId);
}

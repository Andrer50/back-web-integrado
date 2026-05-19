package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.UUID;

public interface ConsultationVitalsRepository {
    ConsultationVitals save(ConsultationVitals vitals);
    List<ConsultationVitals> findByConsultationId(UUID consultationId);
}

package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(UUID id);
    Optional<Prescription> findByConsultationId(UUID consultationId);
    List<Prescription> findAll();
}

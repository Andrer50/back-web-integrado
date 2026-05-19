package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiagnosisRepository {
    Diagnosis save(Diagnosis diagnosis);
    Optional<Diagnosis> findById(UUID id);
    Optional<Diagnosis> findByIcd10(String icd10);
    List<Diagnosis> searchByDescription(String query);
    List<Diagnosis> findAll();
}

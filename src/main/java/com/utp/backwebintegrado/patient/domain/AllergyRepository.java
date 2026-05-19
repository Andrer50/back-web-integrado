package com.utp.backwebintegrado.patient.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AllergyRepository {
    Allergy save(Allergy allergy);
    Optional<Allergy> findById(UUID id);
    List<Allergy> findByPatientId(UUID patientId);
    void deleteById(UUID id);
}

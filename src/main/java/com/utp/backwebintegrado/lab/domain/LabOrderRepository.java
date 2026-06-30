package com.utp.backwebintegrado.lab.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabOrderRepository {
    LabOrder save(LabOrder labOrder);
    Optional<LabOrder> findById(UUID id);
    List<LabOrder> findAll();
    List<LabOrder> findByConsultationId(UUID consultationId);
    List<LabOrder> findByPatientId(UUID patientId);
    List<LabOrder> findByPatientEmail(String email);
    List<LabOrder> findByDoctorEmail(String email);
}

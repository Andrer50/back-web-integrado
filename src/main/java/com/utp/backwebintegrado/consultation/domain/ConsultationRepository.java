package com.utp.backwebintegrado.consultation.domain;

import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository {
    Consultation save(Consultation consultation);
    Optional<Consultation> findById(UUID id);
    Optional<Consultation> findByAppointmentId(UUID appointmentId);
    List<Consultation> findAll();
    List<Consultation> findByDoctorIdAndStatus(UUID doctorId, ConsultationStatus status);
    List<Consultation> findByDoctorId(UUID doctorId);
}
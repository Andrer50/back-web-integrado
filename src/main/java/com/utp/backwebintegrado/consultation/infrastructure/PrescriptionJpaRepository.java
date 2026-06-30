package com.utp.backwebintegrado.consultation.infrastructure;

import com.utp.backwebintegrado.consultation.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionJpaRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findByConsultation_Id(UUID consultationId);
    List<Prescription> findByConsultation_Appointment_Patient_IdOrderByIssueDateDesc(UUID patientId);
}

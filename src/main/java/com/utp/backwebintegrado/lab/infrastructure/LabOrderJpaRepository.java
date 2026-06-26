package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.domain.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabOrderJpaRepository extends JpaRepository<LabOrder, UUID> {
    List<LabOrder> findByConsultation_IdOrderByOrderedAtDesc(UUID consultationId);
    List<LabOrder> findByConsultation_Appointment_Patient_IdOrderByOrderedAtDesc(UUID patientId);
}

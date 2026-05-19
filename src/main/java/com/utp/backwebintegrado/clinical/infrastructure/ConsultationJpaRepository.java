package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationJpaRepository extends JpaRepository<Consultation, UUID> {
    Optional<Consultation> findByAppointment_Id(UUID appointmentId);
    List<Consultation> findByAppointment_Doctor_IdAndStatus(UUID doctorId, com.utp.backwebintegrado.shared.enumeration.ConsultationStatus status);
    List<Consultation> findByAppointment_Doctor_Id(UUID doctorId);
}

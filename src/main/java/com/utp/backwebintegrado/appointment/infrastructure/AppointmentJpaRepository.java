package com.utp.backwebintegrado.appointment.infrastructure;

import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<Appointment, UUID> {
    
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Appointment> searchAppointments(
            @Param("patientId") UUID patientId,
            @Param("doctorId") UUID doctorId,
            @Param("status") AppointmentStatus status,
            Pageable pageable
    );
}

package com.utp.backwebintegrado.appointment.domain;

import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID id);
    List<Appointment> findAll();
    Page<Appointment> findAll(UUID patientId, UUID doctorId, AppointmentStatus status, Pageable pageable);
    void deleteById(UUID id);
}

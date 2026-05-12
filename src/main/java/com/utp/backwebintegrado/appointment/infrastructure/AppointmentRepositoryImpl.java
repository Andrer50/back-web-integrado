package com.utp.backwebintegrado.appointment.infrastructure;

import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentJpaRepository jpaRepository;

    @Override
    public Appointment save(Appointment appointment) {
        return jpaRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Appointment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Appointment> findAll(UUID patientId, UUID doctorId, AppointmentStatus status, Pageable pageable) {
        return jpaRepository.searchAppointments(patientId, doctorId, status, pageable);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Consultation;
import com.utp.backwebintegrado.clinical.domain.ConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;

@Repository
@RequiredArgsConstructor
public class ConsultationRepositoryImpl implements ConsultationRepository {

    private final ConsultationJpaRepository jpaRepository;

    @Override
    public Consultation save(Consultation consultation) {
        return jpaRepository.save(consultation);
    }

    @Override
    public Optional<Consultation> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Consultation> findByAppointmentId(UUID appointmentId) {
        return jpaRepository.findByAppointment_Id(appointmentId);
    }

    @Override
    public List<Consultation> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Consultation> findByDoctorIdAndStatus(UUID doctorId, ConsultationStatus status) {
        return jpaRepository.findByAppointment_Doctor_IdAndStatus(doctorId, status);
    }

    @Override
    public List<Consultation> findByDoctorId(UUID doctorId) {
        return jpaRepository.findByAppointment_Doctor_Id(doctorId);
    }
}

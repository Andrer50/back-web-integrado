package com.utp.backwebintegrado.consultation.infrastructure;

import com.utp.backwebintegrado.consultation.domain.Prescription;
import com.utp.backwebintegrado.consultation.domain.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private final PrescriptionJpaRepository jpaRepository;

    @Override
    public Prescription save(Prescription prescription) {
        return jpaRepository.save(prescription);
    }

    @Override
    public Optional<Prescription> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Prescription> findByConsultationId(UUID consultationId) {
        return jpaRepository.findByConsultation_Id(consultationId);
    }

    @Override
    public List<Prescription> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Prescription> findByPatientId(UUID patientId) {
        return jpaRepository.findByConsultation_Appointment_Patient_IdOrderByIssueDateDesc(patientId);
    }
}

package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LabOrderRepositoryImpl implements LabOrderRepository {

    private final LabOrderJpaRepository jpaRepository;

    @Override
    public LabOrder save(LabOrder labOrder) {
        return jpaRepository.save(labOrder);
    }

    @Override
    public Optional<LabOrder> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<LabOrder> findByPatientId(UUID patientId) {
        return jpaRepository.findByConsultation_Appointment_Patient_IdOrderByOrderedAtDesc(patientId);
    }
}

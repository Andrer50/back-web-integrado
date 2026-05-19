package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Allergy;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AllergyRepositoryImpl implements AllergyRepository {

    private final AllergyJpaRepository jpaRepository;

    @Override
    public Allergy save(Allergy allergy) {
        return jpaRepository.save(allergy);
    }

    @Override
    public Optional<Allergy> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Allergy> findByPatientId(UUID patientId) {
        return jpaRepository.findByPatient_Id(patientId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

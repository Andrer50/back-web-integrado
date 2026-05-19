package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Medication;
import com.utp.backwebintegrado.clinical.domain.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MedicationRepositoryImpl implements MedicationRepository {

    private final MedicationJpaRepository jpaRepository;

    @Override
    public Medication save(Medication medication) {
        return jpaRepository.save(medication);
    }

    @Override
    public Optional<Medication> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Medication> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Medication> searchByName(String query) {
        return jpaRepository.searchByName(query);
    }

    @Override
    public List<Medication> findAll() {
        return jpaRepository.findAll();
    }
}

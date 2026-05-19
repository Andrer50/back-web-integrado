package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Diagnosis;
import com.utp.backwebintegrado.clinical.domain.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DiagnosisRepositoryImpl implements DiagnosisRepository {

    private final DiagnosisJpaRepository jpaRepository;

    @Override
    public Diagnosis save(Diagnosis diagnosis) {
        return jpaRepository.save(diagnosis);
    }

    @Override
    public Optional<Diagnosis> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Diagnosis> findByIcd10(String icd10) {
        return jpaRepository.findByIcd10(icd10);
    }

    @Override
    public List<Diagnosis> searchByDescription(String query) {
        return jpaRepository.searchByDescription(query);
    }

    @Override
    public List<Diagnosis> findAll() {
        return jpaRepository.findAll();
    }
}

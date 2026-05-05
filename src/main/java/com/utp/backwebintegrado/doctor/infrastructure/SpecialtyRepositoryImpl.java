package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.Specialty;
import com.utp.backwebintegrado.doctor.domain.SpecialtyRepository;
import com.utp.backwebintegrado.shared.enumeration.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SpecialtyRepositoryImpl implements SpecialtyRepository {

    private final SpecialtyJpaRepository jpaRepository;

    @Override
    public Specialty save(Specialty specialty) {
        return jpaRepository.save(specialty);
    }

    @Override
    public Optional<Specialty> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Specialty> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Specialty> findAll(String query, Status status, Pageable pageable) {
        return jpaRepository.searchSpecialties(query, status, pageable);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

package com.utp.backwebintegrado.doctor.domain;

import com.utp.backwebintegrado.shared.enumeration.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository {
    Specialty save(Specialty specialty);
    Optional<Specialty> findById(UUID id);
    List<Specialty> findAll();
    Page<Specialty> findAll(String query, Status status, Pageable pageable);
    boolean existsByName(String name);
    void deleteById(UUID id);
}

package com.utp.backwebintegrado.doctor.infrastructure;

import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DoctorRepositoryImpl implements DoctorRepository {

    private final DoctorJpaRepository jpaRepository;

    @Override
    public Doctor save(Doctor doctor) {
        return jpaRepository.save(doctor);
    }

    @Override
    public Optional<Doctor> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Doctor> findAll(String query, Pageable pageable) {
        return jpaRepository.searchDoctors(query, pageable);
    }

    @Override
    public boolean existsByMedicalLicenseNumber(String medicalLicenseNumber) {
        return jpaRepository.existsByMedicalLicenseNumber(medicalLicenseNumber);
    }
}

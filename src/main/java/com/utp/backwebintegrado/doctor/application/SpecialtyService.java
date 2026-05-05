package com.utp.backwebintegrado.doctor.application;

import com.utp.backwebintegrado.doctor.application.dto.SpecialtyRequest;
import com.utp.backwebintegrado.doctor.application.dto.SpecialtyResponse;
import com.utp.backwebintegrado.doctor.domain.Specialty;
import com.utp.backwebintegrado.doctor.domain.SpecialtyRepository;
import com.utp.backwebintegrado.doctor.infrastructure.SpecialtyMapper;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import com.utp.backwebintegrado.shared.enumeration.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialtyService {
    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Transactional(rollbackFor = Exception.class)
    public SpecialtyResponse createSpecialty(SpecialtyRequest request) {
        if (specialtyRepository.existsByName(request.getName())) {
            throw new ApiValidateException("La especialidad ya está registrada.");
        }

        Specialty specialty = specialtyMapper.toEntity(request);
        Specialty saved = specialtyRepository.save(specialty);
        return specialtyMapper.toResponse(saved);
    }

    public List<SpecialtyResponse> findAll() {
        return specialtyRepository.findAll().stream()
                .map(specialtyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<SpecialtyResponse> findAllPaginated(String query, Status status, Pageable pageable) {
        return specialtyRepository.findAll(query, status, pageable)
                .map(specialtyMapper::toResponse);
    }

    public SpecialtyResponse findById(UUID id) {
        return specialtyRepository.findById(id)
                .map(specialtyMapper::toResponse)
                .orElseThrow(() -> new ApiValidateException("Especialidad no encontrada con ID: " + id));
    }

    @Transactional(rollbackFor = Exception.class)
    public SpecialtyResponse updateSpecialty(UUID id, SpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Especialidad no encontrada."));

        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        specialty.setStatus(request.getStatus());

        Specialty updated = specialtyRepository.save(specialty);
        return specialtyMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSpecialty(UUID id) {
        if (specialtyRepository.findById(id).isEmpty()) {
            throw new ApiValidateException("Especialidad no encontrada.");
        }
        specialtyRepository.deleteById(id);
    }
}

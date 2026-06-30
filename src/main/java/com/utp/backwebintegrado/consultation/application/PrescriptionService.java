package com.utp.backwebintegrado.consultation.application;

import com.utp.backwebintegrado.consultation.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.consultation.domain.PrescriptionRepository;
import com.utp.backwebintegrado.consultation.infrastructure.mapper.PrescriptionMapper;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional(readOnly = true)
    public PrescriptionResponse findById(UUID id) {
        return prescriptionRepository.findById(id)
                .map(prescriptionMapper::toResponse)
                .orElseThrow(() -> new ApiValidateException("Receta no encontrada: " + id));
    }
}

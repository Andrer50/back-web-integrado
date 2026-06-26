package com.utp.backwebintegrado.lab.application;

import com.utp.backwebintegrado.clinical.domain.Consultation;
import com.utp.backwebintegrado.clinical.domain.ConsultationRepository;
import com.utp.backwebintegrado.lab.application.dto.LabOrderRequest;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.lab.infrastructure.LabMapper;
import com.utp.backwebintegrado.shared.enumeration.LabOrderStatus;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabOrderService {
    private final LabOrderRepository labOrderRepository;
    private final ConsultationRepository consultationRepository;
    private final LabMapper labMapper;

    @Transactional(rollbackFor = Exception.class)
    public List<LabOrderResponse> createOrders(UUID consultationId, List<LabOrderRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ApiValidateException("Debes indicar al menos un examen de laboratorio o imagen.");
        }

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + consultationId));

        return requests.stream()
                .filter(this::hasName)
                .map(request -> labOrderRepository.save(LabOrder.builder()
                        .consultation(consultation)
                        .type(normalizeType(request.getType()))
                        .name(request.getName().trim())
                        .status(LabOrderStatus.PENDING)
                        .build()))
                .map(labMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> findByConsultationId(UUID consultationId) {
        return labOrderRepository.findByConsultationId(consultationId).stream()
                .map(labMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LabOrderResponse findById(UUID id) {
        return labOrderRepository.findById(id)
                .map(labMapper::toResponse)
                .orElseThrow(() -> new ApiValidateException("Orden de examen no encontrada: " + id));
    }

    private boolean hasName(LabOrderRequest request) {
        return request != null && request.getName() != null && !request.getName().isBlank();
    }

    private String normalizeType(String type) {
        return type != null && !type.isBlank() ? type.trim().toUpperCase() : "LABORATORY";
    }
}

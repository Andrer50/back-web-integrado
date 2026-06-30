package com.utp.backwebintegrado.lab.application;

import com.utp.backwebintegrado.consultation.domain.Consultation;
import com.utp.backwebintegrado.consultation.domain.ConsultationRepository;
import com.utp.backwebintegrado.lab.application.dto.LabOrderRequest;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.lab.application.dto.LabResultRequest;
import com.utp.backwebintegrado.lab.application.dto.LabResultResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.lab.domain.LabResult;
import com.utp.backwebintegrado.lab.domain.LabResultRepository;
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
    private final LabResultRepository labResultRepository;
    private final ConsultationRepository consultationRepository;
    private final LabMapper labMapper;

    @Transactional(rollbackFor = Exception.class)
    public List<LabOrderResponse> createOrders(
            UUID consultationId,
            List<LabOrderRequest> requests,
            String actorEmail,
            List<String> roles) {
        if (requests == null || requests.isEmpty()) {
            throw new ApiValidateException("Debes indicar al menos un examen de laboratorio o imagen.");
        }

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + consultationId));
        assertCanManage(consultation, actorEmail, roles);

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
    public List<LabOrderResponse> findVisibleOrders(String actorEmail, List<String> roles) {
        List<LabOrder> orders;
        if (hasRole(roles, "ADMIN")) {
            orders = labOrderRepository.findAll();
        } else if (hasRole(roles, "PATIENT")) {
            orders = labOrderRepository.findByPatientEmail(actorEmail);
        } else if (hasRole(roles, "DOCTOR")) {
            orders = labOrderRepository.findByDoctorEmail(actorEmail);
        } else {
            throw new ApiValidateException("No tienes permiso para consultar órdenes de exámenes.");
        }

        return orders.stream()
                .map(labMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LabOrderResponse> findByConsultationId(
            UUID consultationId,
            String actorEmail,
            List<String> roles) {
        List<LabOrder> orders = labOrderRepository.findByConsultationId(consultationId);
        orders.forEach(order -> assertCanView(order, actorEmail, roles));
        return orders.stream().map(labMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LabOrderResponse findById(UUID id, String actorEmail, List<String> roles) {
        LabOrder order = getOrder(id);
        assertCanView(order, actorEmail, roles);
        return labMapper.toResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public LabResultResponse recordResult(
            UUID labOrderId,
            LabResultRequest request,
            String actorEmail,
            List<String> roles) {
        if (request == null || request.getDetails() == null || request.getDetails().isBlank()) {
            throw new ApiValidateException("El detalle del resultado es obligatorio.");
        }

        LabOrder labOrder = getOrder(labOrderId);
        assertCanManage(labOrder.getConsultation(), actorEmail, roles);

        if (labOrder.getStatus() == LabOrderStatus.CANCELLED) {
            throw new ApiValidateException("No se puede registrar un resultado para una orden cancelada.");
        }
        if (labResultRepository.findByLabOrderId(labOrderId).isPresent()) {
            throw new ApiValidateException("La orden de examen ya tiene un resultado registrado.");
        }

        LabResult savedResult = labResultRepository.save(LabResult.builder()
                .labOrder(labOrder)
                .details(request.getDetails().trim())
                .build());

        labOrder.setLabResult(savedResult);
        labOrder.setStatus(LabOrderStatus.COMPLETED);
        labOrderRepository.save(labOrder);

        return labMapper.toResultResponse(savedResult);
    }

    private LabOrder getOrder(UUID id) {
        return labOrderRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Orden de examen no encontrada: " + id));
    }

    private void assertCanManage(Consultation consultation, String actorEmail, List<String> roles) {
        if (hasRole(roles, "ADMIN")) {
            return;
        }
        boolean ownsConsultation = hasRole(roles, "DOCTOR")
                && consultation.getAppointment().getDoctor().getUser().getEmail().equalsIgnoreCase(actorEmail);
        if (!ownsConsultation) {
            throw new ApiValidateException("No tienes permiso para modificar esta orden de examen.");
        }
    }

    private void assertCanView(LabOrder order, String actorEmail, List<String> roles) {
        if (hasRole(roles, "ADMIN")) {
            return;
        }

        boolean isOwnerPatient = hasRole(roles, "PATIENT")
                && order.getConsultation().getAppointment().getPatient().getUser().getEmail().equalsIgnoreCase(actorEmail);
        boolean isOwnerDoctor = hasRole(roles, "DOCTOR")
                && order.getConsultation().getAppointment().getDoctor().getUser().getEmail().equalsIgnoreCase(actorEmail);
        if (!isOwnerPatient && !isOwnerDoctor) {
            throw new ApiValidateException("No tienes permiso para consultar esta orden de examen.");
        }
    }

    private boolean hasRole(List<String> roles, String role) {
        return roles != null && roles.contains(role);
    }

    private boolean hasName(LabOrderRequest request) {
        return request != null && request.getName() != null && !request.getName().isBlank();
    }

    private String normalizeType(String type) {
        return type != null && !type.isBlank() ? type.trim().toUpperCase() : "LABORATORY";
    }
}

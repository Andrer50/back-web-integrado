package com.utp.backwebintegrado.audit.application;

import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.audit.application.dto.AuditLogResponse;
import com.utp.backwebintegrado.audit.domain.AuditLog;
import com.utp.backwebintegrado.audit.domain.AuditLogRepository;
import com.utp.backwebintegrado.audit.infrastructure.mapper.AuditLogMapper;
import com.utp.backwebintegrado.consultation.domain.Prescription;
import com.utp.backwebintegrado.consultation.domain.PrescriptionItem;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
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
public class AuditService {
    private static final String MODULE_APPOINTMENTS = "APPOINTMENTS";
    private static final String MODULE_PRESCRIPTIONS = "PRESCRIPTIONS";

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Transactional
    public void recordAppointmentCreated(Appointment appointment, String actorEmail, List<String> roles) {
        record(
                MODULE_APPOINTMENTS,
                "APPOINTMENT",
                appointment.getId(),
                "CREATED",
                "appointment",
                null,
                describeAppointment(appointment),
                actorEmail,
                roles,
                "Cita medica creada"
        );
    }

    @Transactional
    public void recordAppointmentStatusChange(
            Appointment appointment,
            AppointmentStatus oldStatus,
            AppointmentStatus newStatus,
            String actorEmail,
            List<String> roles) {

        record(
                MODULE_APPOINTMENTS,
                "APPOINTMENT",
                appointment.getId(),
                "STATUS_CHANGED",
                "status",
                oldStatus != null ? oldStatus.name() : null,
                newStatus != null ? newStatus.name() : null,
                actorEmail,
                roles,
                "Cambio de estado de cita medica"
        );
    }

    @Transactional
    public void recordPrescriptionCreated(Prescription prescription, String actorEmail, List<String> roles) {
        record(
                MODULE_PRESCRIPTIONS,
                "PRESCRIPTION",
                prescription.getId(),
                "CREATED",
                "items",
                null,
                describePrescription(prescription),
                actorEmail,
                roles,
                "Receta medica creada"
        );
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findAll(
            String module,
            String entityType,
            UUID entityId,
            Pageable pageable,
            List<String> roles) {

        if (roles == null || !roles.contains("ADMIN")) {
            throw new ApiValidateException("Solo administradores pueden consultar auditoria.");
        }

        return auditLogRepository.findAll(normalize(module), normalize(entityType), entityId, pageable)
                .map(auditLogMapper::toResponse);
    }

    private void record(
            String module,
            String entityType,
            UUID entityId,
            String action,
            String fieldName,
            String oldValue,
            String newValue,
            String actorEmail,
            List<String> roles,
            String description) {

        auditLogRepository.save(AuditLog.builder()
                .module(module)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .actorEmail(actorEmail != null && !actorEmail.isBlank() ? actorEmail : "system")
                .actorRoles(roles != null ? String.join(",", roles) : null)
                .description(description)
                .build());
    }

    private String describeAppointment(Appointment appointment) {
        return "status=" + appointment.getStatus()
                + ", date=" + appointment.getAppointmentDate()
                + ", time=" + appointment.getAppointmentTime()
                + ", patient=" + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName()
                + ", doctor=" + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
    }

    private String describePrescription(Prescription prescription) {
        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            return "Receta sin medicamentos registrados";
        }

        return prescription.getItems().stream()
                .map(this::describePrescriptionItem)
                .collect(Collectors.joining("; "));
    }

    private String describePrescriptionItem(PrescriptionItem item) {
        String medicationName = item.getMedication() != null ? item.getMedication().getName() : "Medicamento";
        return medicationName
                + " | dosis=" + nullSafe(item.getDosage())
                + " | frecuencia=" + nullSafe(item.getFrequency())
                + " | duracion=" + nullSafe(item.getDuration());
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private String normalize(String value) {
        return value != null && !value.isBlank() ? value.trim().toUpperCase() : null;
    }
}
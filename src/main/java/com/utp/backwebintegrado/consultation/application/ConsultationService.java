package com.utp.backwebintegrado.consultation.application;

import com.utp.backwebintegrado.audit.application.AuditService;
import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.consultation.application.dto.ConsultationResponse;
import com.utp.backwebintegrado.clinical.domain.*;
import com.utp.backwebintegrado.consultation.domain.Consultation;
import com.utp.backwebintegrado.consultation.domain.ConsultationRepository;
import com.utp.backwebintegrado.consultation.infrastructure.mapper.ConsultationMapper;
import com.utp.backwebintegrado.consultation.application.dto.*;
import com.utp.backwebintegrado.consultation.domain.*;
import com.utp.backwebintegrado.lab.application.dto.LabOrderRequest;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.lab.infrastructure.LabMapper;
import com.utp.backwebintegrado.patient.domain.Allergy;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.shared.enumeration.AllergySeverity;
import com.utp.backwebintegrado.shared.enumeration.DiagnosisType;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;
import com.utp.backwebintegrado.shared.enumeration.LabOrderStatus;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationVitalsRepository vitalsRepository;
    private final ConsultationDiagnosisRepository diagnosisRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AllergyRepository allergyRepository;
    private final MedicationRepository medicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisEntityRepository;
    private final ConsultationMapper consultationMapper;
    private final LabOrderRepository labOrderRepository;
    private final LabMapper labMapper;
    private final AuditService auditService;

    /**
     * Crea una nueva consulta vinculada a una cita médica.
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsultationResponse createConsultation(ConsultationRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ApiValidateException("Cita no encontrada: " + request.getAppointmentId()));

        // Validar que no exista ya una consulta para esta cita
        consultationRepository.findByAppointmentId(request.getAppointmentId())
                .ifPresent(c -> { throw new ApiValidateException("Ya existe una consulta para esta cita."); });

        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .notes(request.getNotes())
                .status(ConsultationStatus.PENDING)
                .build();

        Consultation saved = consultationRepository.save(consultation);
        return consultationMapper.toResponse(saved);
    }

    /**
     * Obtiene la consulta completa por ID de consulta.
     */
    @Transactional(readOnly = true)
    public ConsultationResponse findById(UUID id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + id));
        return toFullResponse(consultation);
    }

    /**
     * Obtiene la consulta completa por ID de la cita (appointmentId).
     */
    @Transactional(readOnly = true)
    public ConsultationResponse findByAppointmentId(UUID appointmentId) {
        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ApiValidateException("No existe consulta para la cita: " + appointmentId));
        return toFullResponse(consultation);
    }

    /**
     * Registra los signos vitales para una consulta existente.
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsultationVitalsResponse addVitals(UUID consultationId, ConsultationVitalsRequest request) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + consultationId));

        ConsultationVitals vitals = ConsultationVitals.builder()
                .consultation(consultation)
                .weight(request.getWeight())
                .height(request.getHeight())
                .bloodPressure(request.getBloodPressure())
                .temperature(request.getTemperature())
                .heartRate(request.getHeartRate())
                .build();

        ConsultationVitals saved = vitalsRepository.save(vitals);
        return consultationMapper.toVitalsResponse(saved);
    }

    /**
     * Agrega un diagnóstico a una consulta (busca o crea el diagnóstico CIE-10).
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsultationDiagnosisResponse addDiagnosis(UUID consultationId, ConsultationDiagnosisRequest request) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + consultationId));

        validatePrimaryDiagnosisLimit(consultationId, List.of(request));
        ConsultationDiagnosis saved = saveDiagnosis(consultation, request);
        return consultationMapper.toDiagnosisResponse(saved);
    }

    /**
     * Endpoint principal: finaliza la consulta completa en un solo llamado.
     * Guarda notas, vitales, diagnóstico, receta médica y alergias.
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsultationResponse completeConsultation(UUID consultationId, CompleteConsultationRequest request, String actorEmail, List<String> roles) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ApiValidateException("Consulta no encontrada: " + consultationId));

        // 1. Actualizar notas
        if (request.getNotes() != null) {
            consultation.setNotes(request.getNotes());
            consultationRepository.save(consultation);
        }

        // Registrar signos vitales
        if (request.getVitals() != null) {
            ConsultationVitalsRequest v = request.getVitals();
            vitalsRepository.save(ConsultationVitals.builder()
                    .consultation(consultation)
                    .weight(v.getWeight())
                    .height(v.getHeight())
                    .bloodPressure(v.getBloodPressure())
                    .temperature(v.getTemperature())
                    .heartRate(v.getHeartRate())
                    .build());
        }

        // 3. Registrar diagnóstico principal y diagnósticos secundarios
        List<ConsultationDiagnosisRequest> diagnosisRequests = getDiagnosisRequests(request);
        validatePrimaryDiagnosisLimit(consultationId, diagnosisRequests);
        diagnosisRequests.forEach(diagnosis -> saveDiagnosis(consultation, diagnosis));

        // Crear receta médica con sus ítems
        if (request.getPrescription() != null && request.getPrescription().getItems() != null
                && !request.getPrescription().getItems().isEmpty()) {

            List<PrescriptionItem> items = new ArrayList<>();
            Prescription prescription = Prescription.builder()
                    .consultation(consultation)
                    .notes(request.getPrescription().getNotes())
                    .items(items)
                    .build();
            Prescription savedPrescription = prescriptionRepository.save(prescription);

            for (PrescriptionItemRequest itemReq : request.getPrescription().getItems()) {
                // Buscar o crear el medicamento por nombre
                Medication medication = medicationRepository.findByName(itemReq.getMedicationName())
                        .orElseGet(() -> medicationRepository.save(
                                Medication.builder()
                                        .name(itemReq.getMedicationName())
                                        .build()
                        ));

                items.add(PrescriptionItem.builder()
                        .prescription(savedPrescription)
                        .medication(medication)
                        .dosage(itemReq.getDosage())
                        .frequency(itemReq.getFrequency())
                        .duration(itemReq.getDuration())
                        .instructions(itemReq.getInstructions())
                        .build());
            }
            // Guarda la prescripción con sus ítems (cascade ALL)
            savedPrescription.setItems(items);
            savedPrescription = prescriptionRepository.save(savedPrescription);
            auditService.recordPrescriptionCreated(savedPrescription, actorEmail, roles);
        }

        // 5. Registrar solicitudes de laboratorio o imagenes
        saveLabOrders(consultation, request.getLabOrders());

        // 6. Registrar alergias del paciente
        if (request.getAllergies() != null && !request.getAllergies().isEmpty()) {
            Patient patient = consultation.getAppointment().getPatient();
            for (CompleteConsultationRequest.AllergyConsultationRequest allergyReq : request.getAllergies()) {
                AllergySeverity severity = AllergySeverity.valueOf(allergyReq.getSeverity().toUpperCase());
                allergyRepository.save(Allergy.builder()
                        .patient(patient)
                        .allergen(allergyReq.getType())
                        .type("Medication")
                        .severity(severity)
                        .build());
            }
        }
        // 7. Actualizar el estado de la cita a COMPLETED
        Appointment appointment = consultation.getAppointment();
        AppointmentStatus previousAppointmentStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        if (previousAppointmentStatus != AppointmentStatus.COMPLETED) {
            auditService.recordAppointmentStatusChange(appointment, previousAppointmentStatus, AppointmentStatus.COMPLETED, actorEmail, roles);
        }

        // 8. Actualizar el estado de la consulta a COMPLETED
        consultation.setStatus(ConsultationStatus.COMPLETED);
        consultationRepository.save(consultation);

        return toFullResponse(consultationRepository.findById(consultationId).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponse> findByDoctorIdAndStatus(UUID doctorId, String statusStr) {
        List<Consultation> consultations;
        if (statusStr != null && !statusStr.isBlank()) {
            ConsultationStatus status = ConsultationStatus.valueOf(statusStr.toUpperCase());
            consultations = consultationRepository.findByDoctorIdAndStatus(doctorId, status);
        } else {
            consultations = consultationRepository.findByDoctorId(doctorId);
        }

        return consultations.stream()
                .map(this::toFullResponse)
                .toList();
    }

    private ConsultationResponse toFullResponse(Consultation c) {
        List<ConsultationVitals> vitalsList = vitalsRepository.findByConsultationId(c.getId());
        List<ConsultationDiagnosis> diagnosesList = diagnosisRepository.findByConsultationId(c.getId());
        Prescription prescription = prescriptionRepository.findByConsultationId(c.getId()).orElse(null);
        List<LabOrderResponse> labOrders = labOrderRepository.findByConsultationId(c.getId()).stream()
                .map(labMapper::toResponse)
                .toList();

        ConsultationVitals vitals = vitalsList.isEmpty() ? null : vitalsList.get(0);

        ConsultationResponse response = consultationMapper.toFullResponse(c, vitals, diagnosesList, prescription);
        response.setLabOrders(labOrders);
        return response;
    }

    private void saveLabOrders(Consultation consultation, List<LabOrderRequest> labOrders) {
        if (labOrders == null || labOrders.isEmpty()) {
            return;
        }

        labOrders.stream()
                .filter(this::hasLabOrderName)
                .map(request -> LabOrder.builder()
                        .consultation(consultation)
                        .type(normalizeLabOrderType(request.getType()))
                        .name(request.getName().trim())
                        .status(LabOrderStatus.PENDING)
                        .build())
                .forEach(labOrderRepository::save);
    }

    private boolean hasLabOrderName(LabOrderRequest request) {
        return request != null && request.getName() != null && !request.getName().isBlank();
    }

    private String normalizeLabOrderType(String type) {
        return type != null && !type.isBlank() ? type.trim().toUpperCase() : "LABORATORY";
    }

    private List<ConsultationDiagnosisRequest> getDiagnosisRequests(CompleteConsultationRequest request) {
        if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
            return request.getDiagnoses().stream()
                    .filter(this::hasDiagnosisCode)
                    .toList();
        }

        return hasDiagnosisCode(request.getDiagnosis())
                ? List.of(request.getDiagnosis())
                : List.of();
    }

    private boolean hasDiagnosisCode(ConsultationDiagnosisRequest request) {
        return request != null && request.getIcd10() != null && !request.getIcd10().isBlank();
    }

    private void validatePrimaryDiagnosisLimit(UUID consultationId, List<ConsultationDiagnosisRequest> requests) {
        List<ConsultationDiagnosis> existingDiagnoses = diagnosisRepository.findByConsultationId(consultationId);
        long existingPrimaryCount = existingDiagnoses.stream()
                .filter(diagnosis -> diagnosis.getType() == DiagnosisType.PRIMARY)
                .count();
        long requestedPrimaryCount = requests.stream()
                .filter(this::hasDiagnosisCode)
                .map(request -> parseDiagnosisType(request.getType()))
                .filter(type -> type == DiagnosisType.PRIMARY)
                .count();

        if (existingPrimaryCount + requestedPrimaryCount > 1) {
            throw new ApiValidateException("La consulta solo puede tener un diagnóstico principal.");
        }

        Set<String> diagnosisCodes = new HashSet<>();
        existingDiagnoses.stream()
                .map(ConsultationDiagnosis::getDiagnosis)
                .filter(java.util.Objects::nonNull)
                .map(Diagnosis::getIcd10)
                .filter(java.util.Objects::nonNull)
                .map(this::normalizeDiagnosisCode)
                .forEach(diagnosisCodes::add);

        for (ConsultationDiagnosisRequest request : requests) {
            if (hasDiagnosisCode(request)
                    && !diagnosisCodes.add(normalizeDiagnosisCode(request.getIcd10()))) {
                throw new ApiValidateException(
                        "El diagnóstico " + request.getIcd10().trim() + " ya está asociado a la consulta."
                );
            }
        }
    }

    private ConsultationDiagnosis saveDiagnosis(Consultation consultation, ConsultationDiagnosisRequest request) {
        String diagnosisCode = normalizeDiagnosisCode(request.getIcd10());
        Diagnosis diagnosisEntity = diagnosisEntityRepository.findByIcd10(diagnosisCode)
                .orElseGet(() -> diagnosisEntityRepository.save(
                        Diagnosis.builder()
                                .icd10(diagnosisCode)
                                .description(request.getDescription())
                                .build()
                ));

        return diagnosisRepository.save(ConsultationDiagnosis.builder()
                .consultation(consultation)
                .diagnosis(diagnosisEntity)
                .type(parseDiagnosisType(request.getType()))
                .build());
    }

    private String normalizeDiagnosisCode(String code) {
        return code.trim().toUpperCase();
    }

    private DiagnosisType parseDiagnosisType(String type) {
        if (type == null || type.isBlank()) {
            return DiagnosisType.PRIMARY;
        }

        return switch (type.trim().toUpperCase()) {
            case "PRIMARY", "PRINCIPAL" -> DiagnosisType.PRIMARY;
            case "SECONDARY", "SECUNDARIO" -> DiagnosisType.SECONDARY;
            default -> throw new ApiValidateException("Tipo de diagnóstico no válido: " + type);
        };
    }
}

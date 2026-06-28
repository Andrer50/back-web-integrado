package com.utp.backwebintegrado.clinical.application;

import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.clinical.application.dto.*;
import com.utp.backwebintegrado.clinical.domain.*;
import com.utp.backwebintegrado.clinical.infrastructure.mapper.ConsultationMapper;
import com.utp.backwebintegrado.patient.domain.Allergy;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.shared.enumeration.AllergySeverity;
import com.utp.backwebintegrado.shared.enumeration.DiagnosisType;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;
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

        // Buscar o crear el diagnóstico CIE-10
        Diagnosis diagnosisEntity = diagnosisEntityRepository.findByIcd10(request.getIcd10())
                .orElseGet(() -> diagnosisEntityRepository.save(
                        Diagnosis.builder()
                                .icd10(request.getIcd10())
                                .description(request.getDescription())
                                .build()
                ));

        DiagnosisType type = DiagnosisType.valueOf(request.getType().toUpperCase());

        ConsultationDiagnosis consultationDiagnosis = ConsultationDiagnosis.builder()
                .consultation(consultation)
                .diagnosis(diagnosisEntity)
                .type(type)
                .build();

        ConsultationDiagnosis saved = diagnosisRepository.save(consultationDiagnosis);
        return consultationMapper.toDiagnosisResponse(saved);
    }

    /**
     * Endpoint principal: finaliza la consulta completa en un solo llamado.
     * Guarda notas, vitales, diagnóstico, receta médica y alergias.
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsultationResponse completeConsultation(UUID consultationId, CompleteConsultationRequest request) {
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

        // Registrar diagnóstico
        if (request.getDiagnosis() != null) {
            ConsultationDiagnosisRequest d = request.getDiagnosis();
            if (d.getIcd10() != null && !d.getIcd10().isBlank()) {
                Diagnosis diagnosisEntity = diagnosisEntityRepository.findByIcd10(d.getIcd10())
                        .orElseGet(() -> diagnosisEntityRepository.save(
                                Diagnosis.builder()
                                        .icd10(d.getIcd10())
                                        .description(d.getDescription())
                                        .build()
                        ));
                DiagnosisType type = d.getType() != null
                        ? DiagnosisType.valueOf(d.getType().toUpperCase())
                        : DiagnosisType.PRIMARY;
                diagnosisRepository.save(ConsultationDiagnosis.builder()
                        .consultation(consultation)
                        .diagnosis(diagnosisEntity)
                        .type(type)
                        .build());
            }
        }

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
            prescriptionRepository.save(savedPrescription);
        }

        // Registrar alergias del paciente
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
        // Actualizar el estado de la cita a COMPLETED
        Appointment appointment = consultation.getAppointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Actualizar el estado de la consulta a COMPLETED
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

        ConsultationVitals vitals = vitalsList.isEmpty() ? null : vitalsList.get(0);

        return consultationMapper.toFullResponse(c, vitals, diagnosesList, prescription);
    }
}

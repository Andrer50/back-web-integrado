package com.utp.backwebintegrado.consultation.infrastructure.mapper;

import com.utp.backwebintegrado.consultation.application.dto.ConsultationResponse;
import com.utp.backwebintegrado.consultation.application.dto.ConsultationVitalsResponse;
import com.utp.backwebintegrado.consultation.application.dto.ConsultationDiagnosisResponse;
import com.utp.backwebintegrado.consultation.domain.Consultation;
import com.utp.backwebintegrado.consultation.domain.ConsultationVitals;
import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosis;
import com.utp.backwebintegrado.consultation.domain.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PrescriptionMapper.class})
public interface ConsultationMapper {
    
    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "patientFirstName", source = "appointment.patient.firstName")
    @Mapping(target = "patientLastName", source = "appointment.patient.lastName")
    @Mapping(target = "appointmentDate", source = "appointment.appointmentDate")
    @Mapping(target = "appointmentTime", source = "appointment.appointmentTime")
    @Mapping(target = "vitals", ignore = true)
    @Mapping(target = "diagnoses", ignore = true)
    @Mapping(target = "prescription", ignore = true)
    @Mapping(target = "labOrders", ignore = true)
    ConsultationResponse toResponse(Consultation consultation);

    @Mapping(target = "id", source = "consultation.id")
    @Mapping(target = "notes", source = "consultation.notes")
    @Mapping(target = "createdAt", source = "consultation.createdAt")
    @Mapping(target = "appointmentId", source = "consultation.appointment.id")
    @Mapping(target = "patientFirstName", source = "consultation.appointment.patient.firstName")
    @Mapping(target = "patientLastName", source = "consultation.appointment.patient.lastName")
    @Mapping(target = "appointmentDate", source = "consultation.appointment.appointmentDate")
    @Mapping(target = "appointmentTime", source = "consultation.appointment.appointmentTime")
    @Mapping(target = "vitals", source = "vitals")
    @Mapping(target = "diagnoses", source = "diagnoses")
    @Mapping(target = "prescription", source = "prescription")
    @Mapping(target = "labOrders", ignore = true)
    ConsultationResponse toFullResponse(Consultation consultation, ConsultationVitals vitals, java.util.List<ConsultationDiagnosis> diagnoses, Prescription prescription);

    ConsultationVitalsResponse toVitalsResponse(ConsultationVitals vitals);

    @Mapping(target = "icd10", source = "diagnosis.icd10")
    @Mapping(target = "description", source = "diagnosis.description")
    @Mapping(target = "type", expression = "java(consultationDiagnosis.getType() != null ? consultationDiagnosis.getType().name() : null)")
    ConsultationDiagnosisResponse toDiagnosisResponse(ConsultationDiagnosis consultationDiagnosis);
}

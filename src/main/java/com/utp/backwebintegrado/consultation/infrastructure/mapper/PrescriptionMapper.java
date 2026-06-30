package com.utp.backwebintegrado.consultation.infrastructure.mapper;

import com.utp.backwebintegrado.consultation.application.dto.PrescriptionItemResponse;
import com.utp.backwebintegrado.consultation.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.consultation.domain.Prescription;
import com.utp.backwebintegrado.consultation.domain.PrescriptionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    @Mapping(target = "doctorFirstName", source = "consultation.appointment.doctor.firstName")
    @Mapping(target = "doctorLastName", source = "consultation.appointment.doctor.lastName")
    @Mapping(target = "doctorSpecialty", expression = "java(getSpecialtyName(prescription.getConsultation() != null && prescription.getConsultation().getAppointment() != null ? prescription.getConsultation().getAppointment().getDoctor() : null))")
    @Mapping(target = "appointmentDate", source = "consultation.appointment.appointmentDate")
    @Mapping(target = "appointmentTime", source = "consultation.appointment.appointmentTime")
    PrescriptionResponse toResponse(Prescription prescription);

    @Mapping(target = "medicationName", source = "medication.name")
    PrescriptionItemResponse toItemResponse(PrescriptionItem item);

    default String getSpecialtyName(com.utp.backwebintegrado.doctor.domain.Doctor doctor) {
        if (doctor == null || doctor.getSpecialties() == null || doctor.getSpecialties().isEmpty()) {
            return "General";
        }
        return doctor.getSpecialties().iterator().next().getName();
    }
}

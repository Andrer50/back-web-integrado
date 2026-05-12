package com.utp.backwebintegrado.appointment.infrastructure.mapper;

import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
import com.utp.backwebintegrado.appointment.domain.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientFirstName", source = "patient.firstName")
    @Mapping(target = "patientLastName", source = "patient.lastName")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorFirstName", source = "doctor.firstName")
    @Mapping(target = "doctorLastName", source = "doctor.lastName")
    @Mapping(target = "doctorMedicalLicenseNumber", source = "doctor.medicalLicenseNumber")
    @Mapping(target = "doctorSpecialty", expression = "java(getSpecialtyName(appointment.getDoctor()))")
    @Mapping(target = "status", expression = "java(appointment.getStatus() != null ? appointment.getStatus().name() : null)")
    AppointmentResponse toResponse(Appointment appointment);

    default String getSpecialtyName(com.utp.backwebintegrado.doctor.domain.Doctor doctor) {
        if (doctor == null || doctor.getSpecialties() == null || doctor.getSpecialties().isEmpty()) {
            return "General";
        }
        return doctor.getSpecialties().iterator().next().getName();
    }
}

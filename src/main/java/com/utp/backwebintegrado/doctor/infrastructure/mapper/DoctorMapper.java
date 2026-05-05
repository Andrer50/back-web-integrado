package com.utp.backwebintegrado.doctor.infrastructure.mapper;

import com.utp.backwebintegrado.doctor.application.dto.DoctorRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorResponse;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.Specialty;
import com.utp.backwebintegrado.user.application.dto.UserResponse;
import com.utp.backwebintegrado.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {SpecialtyMapper.class})
public interface DoctorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "specialties", source = "specialties")
    @Mapping(target = "medicalLicenseNumber", source = "request.medicalLicenseNumber")
    @Mapping(target = "bio", source = "request.bio")
    @Mapping(target = "firstName", source = "request.firstName")
    @Mapping(target = "lastName", source = "request.lastName")
    @Mapping(target = "phone", source = "request.phone")
    Doctor toEntity(DoctorRequest request, User user, Set<Specialty> specialties);

    @Mapping(target = "user", source = "doctor")
    DoctorResponse toResponse(Doctor doctor);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phone", source = "phone")
    UserResponse mapToUserResponse(Doctor doctor);
}

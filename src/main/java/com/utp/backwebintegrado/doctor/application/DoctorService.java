package com.utp.backwebintegrado.doctor.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.doctor.application.dto.DoctorRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorResponse;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import com.utp.backwebintegrado.doctor.domain.Specialty;
import com.utp.backwebintegrado.doctor.domain.SpecialtyRepository;
import com.utp.backwebintegrado.doctor.infrastructure.mapper.DoctorMapper;
import com.utp.backwebintegrado.shared.client.AuthClient;
import com.utp.backwebintegrado.shared.enumeration.Role;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import com.utp.backwebintegrado.user.application.dto.AuthRegisterRequest;
import com.utp.backwebintegrado.user.domain.User;
import com.utp.backwebintegrado.user.domain.UserRepository;
import com.utp.backwebintegrado.user.infrastructure.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    private final AuthClient authClient;
    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
            throw new ApiValidateException("El número de colegiatura ya está registrado.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiValidateException("El correo ya está registrado.");
        }

        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialtyIds() != null) {
            for (UUID specialtyId : request.getSpecialtyIds()) {
                specialties.add(specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new ApiValidateException("Especialidad no encontrada: " + specialtyId)));
            }
        }

        UUID userId = UuidCreator.getTimeOrderedEpoch();
        User shadowUser = userMapper.toEntity(request, userId, Role.DOCTOR.name());
        User savedUser = userRepository.save(shadowUser);

        Doctor doctor = doctorMapper.toEntity(request, savedUser, specialties);
        Doctor savedDoctor = doctorRepository.save(doctor);

        authClient.register(AuthRegisterRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.DOCTOR.name())
                .externalId(userId)
                .build());

        return doctorMapper.toResponse(savedDoctor);
    }

    public Page<DoctorResponse> findAllPaginated(String query, Pageable pageable) {
        return doctorRepository.findAll(query, pageable).map(doctorMapper::toResponse);
    }

    public DoctorResponse findById(UUID id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + id));
    }


    @Transactional(rollbackFor = Exception.class)
    public DoctorResponse updateDoctor(UUID id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + id));

        // Validar que el nuevo número de colegiatura no esté registrado por otro médico
        if (!doctor.getMedicalLicenseNumber().equals(request.getMedicalLicenseNumber()) &&
                doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
            throw new ApiValidateException("El número de colegiatura ya está registrado por otro médico.");
        }

        // Actualizar datos personales
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setPhone(request.getPhone());
        doctor.setMedicalLicenseNumber(request.getMedicalLicenseNumber());
        doctor.setBio(request.getBio());

        // Actualizar especialidades
        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            for (UUID specialtyId : request.getSpecialtyIds()) {
                specialties.add(specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new ApiValidateException("Especialidad no encontrada: " + specialtyId)));
            }
        }
        doctor.setSpecialties(specialties);

        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeStatusDoctor(UUID id, String newStatus) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + id));

        if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")) {
            throw new ApiValidateException("Status debe ser ACTIVE o INACTIVE");
        }

        doctor.getUser().setStatus(newStatus);
        userRepository.save(doctor.getUser());
    }
}

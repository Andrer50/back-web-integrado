package com.utp.backwebintegrado.service.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.client.AuthClient;
import com.utp.backwebintegrado.dto.enumeration.Role;
import com.utp.backwebintegrado.dto.request.AuthRegisterRequest;
import com.utp.backwebintegrado.dto.request.PatientRegisterRequest;
import com.utp.backwebintegrado.dto.response.PatientRegisterResponse;
import com.utp.backwebintegrado.entity.Patient;
import com.utp.backwebintegrado.entity.User;
import com.utp.backwebintegrado.exception.ApiValidateException;
import com.utp.backwebintegrado.mapper.PatientMapper;
import com.utp.backwebintegrado.repository.PatientRepository;
import com.utp.backwebintegrado.repository.UserRepository;
import com.utp.backwebintegrado.service.PatientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuthClient authClient;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientRegisterResponse createPatient(PatientRegisterRequest request) {

        // 1. Validaciones de negocio locales
        if (patientRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ApiValidateException("El documento ya está registrado.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiValidateException("El correo ya está registrado en el sistema.");
        }

        // 2. Generar el UUID (externalId) desde el microservicio de negocio
        UUID userId = UuidCreator.getTimeOrderedEpoch();

        // 3. Llamada síncrona al microservicio de Auth
        // Si esta llamada falla (ej. correo duplicado en Auth o servicio caído),
        // lanzará una excepción y el @Transactional cancelará cualquier guardado local.
        AuthRegisterRequest authRequest = AuthRegisterRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.PATIENT.name())
                .externalId(userId)
                .build();

        authClient.register(authRequest);

        // 4. Crear el Shadow User en la base de datos local
        User shadowUser = User.builder()
                .id(userId) // Usamos el ID generado en el paso 2
                .email(request.getEmail())
                .role(Role.PATIENT.name())
                .status("ACTIVE")
                // createdAt y updatedAt se llenan en el @PrePersist de tu entidad
                .build();

        shadowUser = userRepository.save(shadowUser);

        // 5. Crear la entidad Patient vinculada al Shadow User
        Patient patient = Patient.builder()
                // El ID del paciente se generará automáticamente en su propio @PrePersist
                .user(shadowUser)
                .documentNumber(request.getDocumentNumber())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .build();

        patient = patientRepository.save(patient);

        // 6. Retornar respuesta
        return patientMapper.toResponse(patient);
    }
}
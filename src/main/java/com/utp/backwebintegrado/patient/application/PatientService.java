package com.utp.backwebintegrado.patient.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.patient.infrastructure.PatientMapper;
import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.shared.client.AuthClient;
import com.utp.backwebintegrado.shared.enumeration.Role;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import com.utp.backwebintegrado.user.application.dto.AuthRegisterRequest;
import com.utp.backwebintegrado.user.domain.User;
import com.utp.backwebintegrado.user.domain.UserRepository;
import com.utp.backwebintegrado.user.infrastructure.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuthClient authClient;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    public PatientResponse createPatient(PatientRequest request) {

        // Validaciones
        if (patientRepository.existsByDocumentNumber(request.getDocumentNumber()))
            throw new ApiValidateException("El documento ya está registrado.");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new ApiValidateException("El correo ya está registrado.");

        UUID userId = UuidCreator.getTimeOrderedEpoch();

        // Mapper convierte Request → Entity
        User shadowUser = userMapper.toEntity(request, userId, Role.PATIENT.name());
        userRepository.save(shadowUser);

        Patient patient = patientMapper.toEntity(request, shadowUser);
        Patient saved = patientRepository.save(patient);

        // Llama auth al final
        authClient.register(AuthRegisterRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.PATIENT.name())
                .externalId(userId)
                .build());

        // Mapper convierte Entity → Response
        return patientMapper.toResponse(saved);
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse) // De Dominio/Entity a DTO
                .collect(Collectors.toList());
    }

    // CORREGIDO: Usa UUID, patientRepository y mapea a DTO
    public PatientResponse findById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse) // De Dominio/Entity a DTO
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + id));
    }
}

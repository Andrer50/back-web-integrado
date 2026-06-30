package com.utp.backwebintegrado.patient.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.patient.infrastructure.mapper.PatientMapper;
import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.patient.application.dto.PatientMedicalHistoryResponse;
import com.utp.backwebintegrado.patient.application.dto.AllergyResponse;
import com.utp.backwebintegrado.consultation.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import com.utp.backwebintegrado.consultation.domain.PrescriptionRepository;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.patient.infrastructure.mapper.AllergyMapper;
import com.utp.backwebintegrado.consultation.infrastructure.mapper.PrescriptionMapper;
import com.utp.backwebintegrado.lab.infrastructure.LabMapper;
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
    private final AllergyRepository allergyRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabOrderRepository labOrderRepository;
    private final AllergyMapper allergyMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final LabMapper labMapper;

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
        User savedUser = userRepository.save(shadowUser);

        Patient patient = patientMapper.toEntity(request, savedUser);
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

    // Metodo para editar paciente
    @Transactional(rollbackFor = Exception.class)
    public PatientResponse updatePatient(UUID id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + id));

        if (!patient.getDocumentNumber().equals(request.getDocumentNumber()) &&
                patientRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ApiValidateException("El documento ya está registrado.");
        }

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDocumentNumber(request.getDocumentNumber());
        patient.setPhone(request.getPhone());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());

        Patient updated = patientRepository.save(patient);
        return patientMapper.toResponse(updated);
    }

    // Metodo para desactivar/activar paciente
    @Transactional(rollbackFor = Exception.class)
    public void changePatientStatus(UUID id, String newStatus) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + id));

        if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE")) {
            throw new ApiValidateException("Status debe ser ACTIVE o INACTIVE");
        }

        patient.getUser().setStatus(newStatus);
        userRepository.save(patient.getUser());
    }

    public List<PatientResponse> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse) // De Dominio/Entity a DTO
                .collect(Collectors.toList());
    }

    public Page<PatientResponse> findAllPaginated(UUID userId, String query, String status, Pageable pageable) {
        return patientRepository.findAll(userId, query, status, pageable)
                .map(patientMapper::toResponse);
    }

    // CORREGIDO: Usa UUID, patientRepository y mapea a DTO
    public PatientResponse findById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toResponse) // De Dominio/Entity a DTO
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + id));
    }

    public PatientResponse findByUserId(UUID userId) {
        return patientRepository.findByUserId(userId)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado para el usuario ID: " + userId));
    }

    @Transactional(readOnly = true)
    public PatientMedicalHistoryResponse getMedicalHistory(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + id));

        PatientResponse patientResponse = patientMapper.toResponse(patient);

        List<AllergyResponse> allergies = allergyRepository.findByPatientId(id).stream()
                .map(allergyMapper::toResponse)
                .toList();

        List<PrescriptionResponse> prescriptions = prescriptionRepository.findByPatientId(id).stream()
                .map(prescriptionMapper::toResponse)
                .toList();

        List<LabOrderResponse> labOrders = labOrderRepository.findByPatientId(id).stream()
                .map(labMapper::toResponse)
                .toList();

        return PatientMedicalHistoryResponse.builder()
                .patient(patientResponse)
                .allergies(allergies)
                .prescriptions(prescriptions)
                .labOrders(labOrders)
                .build();
    }
}

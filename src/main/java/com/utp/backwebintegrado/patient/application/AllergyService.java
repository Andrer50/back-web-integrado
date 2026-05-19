package com.utp.backwebintegrado.patient.application;

import com.utp.backwebintegrado.patient.application.dto.AllergyRequest;
import com.utp.backwebintegrado.patient.application.dto.AllergyResponse;
import com.utp.backwebintegrado.patient.domain.Allergy;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.patient.infrastructure.mapper.AllergyMapper;
import com.utp.backwebintegrado.shared.enumeration.AllergySeverity;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;
    private final AllergyMapper allergyMapper;

    @Transactional(rollbackFor = Exception.class)
    public AllergyResponse create(AllergyRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado: " + request.getPatientId()));

        Allergy allergy = Allergy.builder()
                .patient(patient)
                .allergen(request.getType())
                .type("Medication")
                .severity(AllergySeverity.valueOf(request.getSeverity().toUpperCase()))
                .build();

        return allergyMapper.toResponse(allergyRepository.save(allergy));
    }

    @Transactional(readOnly = true)
    public List<AllergyResponse> findByPatientId(UUID patientId) {
        return allergyRepository.findByPatientId(patientId).stream()
                .map(allergyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID id) {
        allergyRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Alergia no encontrada: " + id));
        allergyRepository.deleteById(id);
    }
}

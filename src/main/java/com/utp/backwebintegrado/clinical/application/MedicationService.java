package com.utp.backwebintegrado.clinical.application;

import com.utp.backwebintegrado.clinical.application.dto.MedicationResponse;
import com.utp.backwebintegrado.clinical.domain.Medication;
import com.utp.backwebintegrado.clinical.domain.MedicationRepository;
import com.utp.backwebintegrado.clinical.infrastructure.mapper.MedicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Transactional(readOnly = true)
    public List<MedicationResponse> search(String query) {
        List<Medication> medications = (query == null || query.isBlank())
                ? medicationRepository.findAll()
                : medicationRepository.searchByName(query);
        return medications.stream().map(medicationMapper::toResponse).collect(Collectors.toList());
    }
}

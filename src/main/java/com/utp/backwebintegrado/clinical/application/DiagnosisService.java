package com.utp.backwebintegrado.clinical.application;

import com.utp.backwebintegrado.clinical.application.dto.DiagnosisResponse;
import com.utp.backwebintegrado.clinical.domain.Diagnosis;
import com.utp.backwebintegrado.clinical.domain.DiagnosisRepository;
import com.utp.backwebintegrado.clinical.infrastructure.mapper.DiagnosisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisMapper diagnosisMapper;

    @Transactional(readOnly = true)
    public List<DiagnosisResponse> search(String query) {
        List<Diagnosis> results = (query == null || query.isBlank())
                ? diagnosisRepository.findAll()
                : diagnosisRepository.searchByDescription(query);
        return results.stream().map(diagnosisMapper::toResponse).collect(Collectors.toList());
    }
}

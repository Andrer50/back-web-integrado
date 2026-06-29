package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.domain.LabResult;
import com.utp.backwebintegrado.lab.domain.LabResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LabResultRepositoryImpl implements LabResultRepository {
    private final LabResultJpaRepository jpaRepository;

    @Override
    public LabResult save(LabResult labResult) {
        return jpaRepository.save(labResult);
    }

    @Override
    public Optional<LabResult> findByLabOrderId(UUID labOrderId) {
        return jpaRepository.findByLabOrder_Id(labOrderId);
    }
}

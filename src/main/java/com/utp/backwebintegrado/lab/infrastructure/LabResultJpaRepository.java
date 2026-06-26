package com.utp.backwebintegrado.lab.infrastructure;

import com.utp.backwebintegrado.lab.domain.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LabResultJpaRepository extends JpaRepository<LabResult, UUID> {
    Optional<LabResult> findByLabOrder_Id(UUID labOrderId);
}

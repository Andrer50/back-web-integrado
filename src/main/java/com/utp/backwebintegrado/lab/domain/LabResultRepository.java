package com.utp.backwebintegrado.lab.domain;

import java.util.Optional;
import java.util.UUID;

public interface LabResultRepository {
    LabResult save(LabResult labResult);
    Optional<LabResult> findByLabOrderId(UUID labOrderId);
}

package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository {
    Branch save(Branch branch);
    Optional<Branch> findById(UUID id);
    List<Branch> findAll();
    void deleteById(UUID id);
}

package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchJpaRepository extends JpaRepository<Branch, UUID> {
}

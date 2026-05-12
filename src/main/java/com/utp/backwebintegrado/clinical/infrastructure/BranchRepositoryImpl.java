package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.Branch;
import com.utp.backwebintegrado.clinical.domain.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryImpl implements BranchRepository {

    private final BranchJpaRepository jpaRepository;

    @Override
    public Branch save(Branch branch) {
        return jpaRepository.save(branch);
    }

    @Override
    public Optional<Branch> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Branch> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

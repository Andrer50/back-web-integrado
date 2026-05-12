package com.utp.backwebintegrado.clinical.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.clinical.application.dto.BranchRequest;
import com.utp.backwebintegrado.clinical.application.dto.BranchResponse;
import com.utp.backwebintegrado.clinical.domain.Branch;
import com.utp.backwebintegrado.clinical.domain.BranchRepository;
import com.utp.backwebintegrado.clinical.infrastructure.mapper.BranchMapper;
import com.utp.backwebintegrado.shared.enumeration.Status;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Transactional(rollbackFor = Exception.class)
    public BranchResponse createBranch(BranchRequest request) {
        Branch branch = Branch.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .name(request.getName())
                .address(request.getAddress())
                .status(Status.ACTIVE)
                .build();

        Branch saved = branchRepository.save(branch);
        return branchMapper.toResponse(saved);
    }

    public List<BranchResponse> findAll() {
        return branchRepository.findAll().stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BranchResponse findById(UUID id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ApiValidateException("Sede no encontrada con ID: " + id));
        return branchMapper.toResponse(branch);
    }
}

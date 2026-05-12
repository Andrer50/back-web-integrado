package com.utp.backwebintegrado.clinical.infrastructure.mapper;

import com.utp.backwebintegrado.clinical.application.dto.BranchResponse;
import com.utp.backwebintegrado.clinical.domain.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "status", expression = "java(branch.getStatus() != null ? branch.getStatus().name() : null)")
    BranchResponse toResponse(Branch branch);
}

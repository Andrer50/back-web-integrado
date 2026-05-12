package com.utp.backwebintegrado.clinical.infrastructure.mapper;

import com.utp.backwebintegrado.clinical.application.dto.ConsultingRoomResponse;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultingRoomMapper {
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "status", expression = "java(room.getStatus() != null ? room.getStatus().name() : null)")
    ConsultingRoomResponse toResponse(ConsultingRoom room);
}

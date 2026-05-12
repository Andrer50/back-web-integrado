package com.utp.backwebintegrado.clinical.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultingRoomRepository {
    ConsultingRoom save(ConsultingRoom consultingRoom);
    Optional<ConsultingRoom> findById(UUID id);
    List<ConsultingRoom> findAll();
    List<ConsultingRoom> findByBranchId(UUID branchId);
    void deleteById(UUID id);
}

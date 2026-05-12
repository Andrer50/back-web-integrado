package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultingRoomJpaRepository extends JpaRepository<ConsultingRoom, UUID> {
    List<ConsultingRoom> findByBranchId(UUID branchId);
}

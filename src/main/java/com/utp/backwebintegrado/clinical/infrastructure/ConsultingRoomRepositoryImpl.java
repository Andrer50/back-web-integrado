package com.utp.backwebintegrado.clinical.infrastructure;

import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConsultingRoomRepositoryImpl implements ConsultingRoomRepository {

    private final ConsultingRoomJpaRepository jpaRepository;

    @Override
    public ConsultingRoom save(ConsultingRoom consultingRoom) {
        return jpaRepository.save(consultingRoom);
    }

    @Override
    public Optional<ConsultingRoom> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ConsultingRoom> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<ConsultingRoom> findByBranchId(UUID branchId) {
        return jpaRepository.findByBranchId(branchId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

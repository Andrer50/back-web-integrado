package com.utp.backwebintegrado.clinical.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.clinical.application.dto.ConsultingRoomRequest;
import com.utp.backwebintegrado.clinical.application.dto.ConsultingRoomResponse;
import com.utp.backwebintegrado.clinical.domain.Branch;
import com.utp.backwebintegrado.clinical.domain.BranchRepository;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoomRepository;
import com.utp.backwebintegrado.clinical.infrastructure.mapper.ConsultingRoomMapper;
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
public class ConsultingRoomService {

    private final ConsultingRoomRepository roomRepository;
    private final BranchRepository branchRepository;
    private final ConsultingRoomMapper roomMapper;

    @Transactional(rollbackFor = Exception.class)
    public ConsultingRoomResponse createRoom(ConsultingRoomRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ApiValidateException("Sede no encontrada con ID: " + request.getBranchId()));

        ConsultingRoom room = ConsultingRoom.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .branch(branch)
                .roomNumber(request.getRoomNumber())
                .status(Status.ACTIVE)
                .build();

        ConsultingRoom saved = roomRepository.save(room);
        return roomMapper.toResponse(saved);
    }

    public List<ConsultingRoomResponse> findAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ConsultingRoomResponse> findByBranchId(UUID branchId) {
        return roomRepository.findByBranchId(branchId).stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }
}

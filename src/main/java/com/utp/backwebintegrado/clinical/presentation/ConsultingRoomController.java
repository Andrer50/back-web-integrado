package com.utp.backwebintegrado.clinical.presentation;

import com.utp.backwebintegrado.clinical.application.ConsultingRoomService;
import com.utp.backwebintegrado.clinical.application.dto.ConsultingRoomRequest;
import com.utp.backwebintegrado.clinical.application.dto.ConsultingRoomResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consulting-rooms")
@RequiredArgsConstructor
public class ConsultingRoomController {

    private final ConsultingRoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<ConsultingRoomResponse>> create(@RequestBody ConsultingRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConsultingRoomResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(roomService.createRoom(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsultingRoomResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.<List<ConsultingRoomResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(roomService.findAll())
                .build());
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<ConsultingRoomResponse>>> findByBranchId(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.<List<ConsultingRoomResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(roomService.findByBranchId(branchId))
                .build());
    }
}

package com.utp.backwebintegrado.lab.presentation;

import com.utp.backwebintegrado.lab.application.LabOrderService;
import com.utp.backwebintegrado.lab.application.dto.LabOrderRequest;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lab-orders")
@RequiredArgsConstructor
public class LabController {
    private final LabOrderService labOrderService;

    @PostMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<List<LabOrderResponse>>> createOrders(
            @PathVariable UUID consultationId,
            @RequestBody List<LabOrderRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<LabOrderResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(labOrderService.createOrders(consultationId, requests))
                .build());
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<ApiResponse<List<LabOrderResponse>>> findByConsultationId(@PathVariable UUID consultationId) {
        return ResponseEntity.ok(ApiResponse.<List<LabOrderResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(labOrderService.findByConsultationId(consultationId))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabOrderResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<LabOrderResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(labOrderService.findById(id))
                .build());
    }
}

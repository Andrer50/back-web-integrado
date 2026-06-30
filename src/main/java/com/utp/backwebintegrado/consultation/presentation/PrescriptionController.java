package com.utp.backwebintegrado.consultation.presentation;

import com.utp.backwebintegrado.consultation.application.PrescriptionService;
import com.utp.backwebintegrado.consultation.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<PrescriptionResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(prescriptionService.findById(id))
                .build());
    }
}

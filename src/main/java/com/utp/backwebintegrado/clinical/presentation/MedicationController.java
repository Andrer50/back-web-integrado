package com.utp.backwebintegrado.clinical.presentation;

import com.utp.backwebintegrado.clinical.application.MedicationService;
import com.utp.backwebintegrado.clinical.application.dto.MedicationResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> search(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.<List<MedicationResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(medicationService.search(q))
                .build());
    }
}

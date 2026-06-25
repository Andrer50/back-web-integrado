package com.utp.backwebintegrado.patient.presentation;

import com.utp.backwebintegrado.patient.application.PatientService;
import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.patient.application.dto.PatientMedicalHistoryResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> create(@RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<PatientResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.createPatient(request))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> update(
            @PathVariable UUID id,
            @RequestBody PatientRequest request) {
        return ResponseEntity.ok(ApiResponse.<PatientResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.updatePatient(id, request))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<PatientResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> findAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<PatientResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.findAllPaginated(userId, query, status, pageable))
                .build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        patientService.changePatientStatus(id, status);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .build());
    }

    @GetMapping("/{id}/medical-history")
    public ResponseEntity<ApiResponse<PatientMedicalHistoryResponse>> getMedicalHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<PatientMedicalHistoryResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.getMedicalHistory(id))
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PatientResponse>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.<PatientResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.findByUserId(userId))
                .build());
    }
}

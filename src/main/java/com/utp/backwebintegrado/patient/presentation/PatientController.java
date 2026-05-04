package com.utp.backwebintegrado.patient.presentation;

import com.utp.backwebintegrado.patient.application.PatientService;
import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<PatientResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.findAllPaginated(query, status, pageable))
                .build());
    }
}

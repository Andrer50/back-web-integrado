package com.utp.backwebintegrado.patient.presentation;

import com.utp.backwebintegrado.patient.application.AllergyService;
import com.utp.backwebintegrado.patient.application.dto.AllergyRequest;
import com.utp.backwebintegrado.patient.application.dto.AllergyResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping
    public ResponseEntity<ApiResponse<AllergyResponse>> create(@RequestBody AllergyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AllergyResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(allergyService.create(request))
                .build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AllergyResponse>>> findByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.<List<AllergyResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(allergyService.findByPatientId(patientId))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        allergyService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ConstantUtil.OK_CODE)
                .message("Alergia eliminada correctamente")
                .build());
    }
}

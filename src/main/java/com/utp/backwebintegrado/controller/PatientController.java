package com.utp.backwebintegrado.controller;

import com.utp.backwebintegrado.dto.request.AuthRegisterRequest;
import com.utp.backwebintegrado.dto.request.PatientRegisterRequest;
import com.utp.backwebintegrado.dto.response.ApiResponse;
import com.utp.backwebintegrado.dto.response.PatientRegisterResponse;
import com.utp.backwebintegrado.service.PatientService;
import com.utp.backwebintegrado.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientRegisterResponse>> create(@RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<PatientRegisterResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(patientService.createPatient(request))
                .build());
    }
}

package com.utp.backwebintegrado.doctor.presentation;

import com.utp.backwebintegrado.doctor.application.DoctorService;
import com.utp.backwebintegrado.doctor.application.dto.DoctorRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
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
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponse>> create(@RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<DoctorResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(doctorService.createDoctor(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorResponse>>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<DoctorResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(doctorService.findAllPaginated(query, pageable))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<DoctorResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(doctorService.findById(id))
                .build());
    }
}

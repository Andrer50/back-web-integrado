package com.utp.backwebintegrado.doctor.presentation;

import com.utp.backwebintegrado.doctor.application.SpecialtyService;
import com.utp.backwebintegrado.doctor.application.dto.SpecialtyRequest;
import com.utp.backwebintegrado.doctor.application.dto.SpecialtyResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import com.utp.backwebintegrado.shared.enumeration.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
public class SpecialtyController {
    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<ApiResponse<SpecialtyResponse>> create(@RequestBody SpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SpecialtyResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(specialtyService.createSpecialty(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SpecialtyResponse>>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<SpecialtyResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(specialtyService.findAllPaginated(query, status, pageable))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<SpecialtyResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(specialtyService.findById(id))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> update(@PathVariable UUID id, @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(ApiResponse.<SpecialtyResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(specialtyService.updateSpecialty(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .build());
    }
}

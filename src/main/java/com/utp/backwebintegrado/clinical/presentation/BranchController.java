package com.utp.backwebintegrado.clinical.presentation;

import com.utp.backwebintegrado.clinical.application.BranchService;
import com.utp.backwebintegrado.clinical.application.dto.BranchRequest;
import com.utp.backwebintegrado.clinical.application.dto.BranchResponse;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponse>> create(@RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BranchResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(branchService.createBranch(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.<List<BranchResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(branchService.findAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<BranchResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(branchService.findById(id))
                .build());
    }
}

package com.utp.backwebintegrado.controller;

import com.utp.backwebintegrado.dto.request.PatientRegisterRequest;
import com.utp.backwebintegrado.dto.response.ApiResponse;
import com.utp.backwebintegrado.dto.response.PatientRegisterResponse;
import com.utp.backwebintegrado.dto.response.UserResponse;
import com.utp.backwebintegrado.service.UserService;
import com.utp.backwebintegrado.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(userService.getByEmail(email))
                .build());
    }
}

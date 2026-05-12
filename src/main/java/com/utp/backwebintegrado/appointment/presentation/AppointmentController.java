package com.utp.backwebintegrado.appointment.presentation;

import com.utp.backwebintegrado.appointment.application.AppointmentService;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentRequest;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
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
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AppointmentResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(appointmentService.createAppointment(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> findAll(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<AppointmentResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(appointmentService.findAllPaginated(patientId, doctorId, status, pageable))
                .build());
    }
}

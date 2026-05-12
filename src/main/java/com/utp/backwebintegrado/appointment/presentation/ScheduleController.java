package com.utp.backwebintegrado.appointment.presentation;

import com.utp.backwebintegrado.appointment.application.ScheduleService;
import com.utp.backwebintegrado.appointment.application.dto.AvailableDoctorSlotsResponse;
import com.utp.backwebintegrado.appointment.application.dto.DoctorScheduleSlotResponse;
import com.utp.backwebintegrado.appointment.application.dto.GenerateSlotsRequest;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<DoctorScheduleSlotResponse>>> generate(@RequestBody GenerateSlotsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<DoctorScheduleSlotResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(scheduleService.generateSlots(request))
                .build());
    }

    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableDoctorSlotsResponse>>> getAvailableSlots(
            @RequestParam UUID specialtyId,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(ApiResponse.<List<AvailableDoctorSlotsResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(scheduleService.findAvailableSlotsGrouped(specialtyId, branchId, startDate, endDate))
                .build());
    }
}

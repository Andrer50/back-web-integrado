package com.utp.backwebintegrado.appointment.presentation;

import com.utp.backwebintegrado.appointment.application.ScheduleService;
import com.utp.backwebintegrado.appointment.application.dto.AvailableDoctorSlotsResponse;
import com.utp.backwebintegrado.appointment.application.dto.DoctorScheduleSlotResponse;
import com.utp.backwebintegrado.appointment.application.dto.GenerateSlotsRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorScheduleRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorScheduleResponse;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDayRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDayResponse;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDaySaveResponse;
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
public class    ScheduleController {

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

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<DoctorScheduleSlotResponse>>> getSlotsByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        return ResponseEntity.ok(ApiResponse.<List<DoctorScheduleSlotResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(scheduleService.findSlotsByDoctor(doctorId, startDate))
                .build());
    }

    @GetMapping("/doctor/{doctorId}/weekly-config")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> getWeeklyConfig(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(ApiResponse.<List<DoctorScheduleResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(scheduleService.getWeeklyConfigs(doctorId))
                .build());
    }

    @PostMapping("/doctor/{doctorId}/weekly-config")
    public ResponseEntity<ApiResponse<List<DoctorScheduleResponse>>> saveWeeklyConfig(
            @PathVariable UUID doctorId,
            @RequestBody List<DoctorScheduleRequest> request
    ) {
        return ResponseEntity.ok(ApiResponse.<List<DoctorScheduleResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message("Configuración semanal guardada exitosamente")
                .data(scheduleService.saveWeeklyConfigs(doctorId, request))
                .build());
    }

    @GetMapping("/doctor/{doctorId}/off-days")
    public ResponseEntity<ApiResponse<List<DoctorOffDayResponse>>> getOffDays(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(ApiResponse.<List<DoctorOffDayResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(scheduleService.getOffDays(doctorId))
                .build());
    }

    @PostMapping("/doctor/{doctorId}/off-days")
    public ResponseEntity<ApiResponse<DoctorOffDaySaveResponse>> saveOffDay(
            @PathVariable UUID doctorId,
            @RequestBody DoctorOffDayRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<DoctorOffDaySaveResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message("Día libre registrado exitosamente")
                .data(scheduleService.saveOffDay(doctorId, request))
                .build());
    }

    @DeleteMapping("/doctor/off-days/{offDayId}")
    public ResponseEntity<ApiResponse<Void>> deleteOffDay(@PathVariable UUID offDayId) {
        scheduleService.deleteOffDay(offDayId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(ConstantUtil.OK_CODE)
                .message("Día libre eliminado exitosamente")
                .build());
    }
}


package com.utp.backwebintegrado.doctor.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleResponse {
    private UUID id;
    private UUID doctorId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID consultingRoomId;
    private String consultingRoomNumber;
    private String branchName;
    private int slotDurationMinutes;

    @JsonProperty("isActive")
    private boolean isActive;
}

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
public class DoctorScheduleRequest {
    private UUID id;
    private String dayOfWeek; // MONDAY, TUESDAY, etc.
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID consultingRoomId;
    private int slotDurationMinutes;

    @JsonProperty("isActive")
    private boolean isActive;
}

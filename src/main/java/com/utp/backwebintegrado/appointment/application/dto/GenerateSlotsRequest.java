package com.utp.backwebintegrado.appointment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSlotsRequest {
    private UUID doctorId;
    private UUID consultingRoomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes; // e.g., 15, 20 o 30
}

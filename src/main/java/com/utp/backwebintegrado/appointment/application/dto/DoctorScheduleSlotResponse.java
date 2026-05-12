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
public class DoctorScheduleSlotResponse {
    private UUID id;
    private UUID doctorId;
    private String doctorName;
    private UUID consultingRoomId;
    private String roomNumber;
    private String branchName;
    private String branchAddress;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}

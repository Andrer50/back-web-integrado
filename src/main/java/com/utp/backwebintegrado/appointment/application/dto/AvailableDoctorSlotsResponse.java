package com.utp.backwebintegrado.appointment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDoctorSlotsResponse {
    private UUID doctorId;
    private String doctorName;
    private String cmp;
    private String specialty;
    private String branchName;
    private String branchAddress;
    private String modality;
    private List<DateGroup> availableDates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateGroup {
        private String date; // "2026-05-14"
        private String dayLabel; // "Jue"
        private String dateLabel; // "14 May"
        private List<SlotItem> slots;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotItem {
        private UUID slotId;
        private String time; // "15:00"
    }
}

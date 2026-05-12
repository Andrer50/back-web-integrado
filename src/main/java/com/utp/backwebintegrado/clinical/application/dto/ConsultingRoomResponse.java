package com.utp.backwebintegrado.clinical.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultingRoomResponse {
    private UUID id;
    private UUID branchId;
    private String branchName;
    private String roomNumber;
    private String status;
}

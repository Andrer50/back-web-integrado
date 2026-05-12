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
public class ConsultingRoomRequest {
    private UUID branchId;
    private String roomNumber;
}

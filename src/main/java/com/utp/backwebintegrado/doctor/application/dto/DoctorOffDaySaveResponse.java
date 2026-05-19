package com.utp.backwebintegrado.doctor.application.dto;

import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorOffDaySaveResponse {
    private DoctorOffDayResponse offDay;
    private List<AppointmentResponse> conflicts;
}

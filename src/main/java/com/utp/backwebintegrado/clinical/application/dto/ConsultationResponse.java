package com.utp.backwebintegrado.clinical.application.dto;

import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConsultationResponse {
    private UUID id;
    private UUID appointmentId;
    private String patientFirstName;
    private String patientLastName;
    private java.time.LocalDate appointmentDate;
    private java.time.LocalTime appointmentTime;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
    private ConsultationVitalsResponse vitals;
    private List<ConsultationDiagnosisResponse> diagnoses;
    private PrescriptionResponse prescription;
    private List<LabOrderResponse> labOrders;
}

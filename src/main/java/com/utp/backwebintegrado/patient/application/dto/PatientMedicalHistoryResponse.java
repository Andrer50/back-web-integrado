package com.utp.backwebintegrado.patient.application.dto;

import com.utp.backwebintegrado.clinical.application.dto.PrescriptionResponse;
import com.utp.backwebintegrado.lab.application.dto.LabOrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicalHistoryResponse {
    private PatientResponse patient;
    private List<AllergyResponse> allergies;
    private List<PrescriptionResponse> prescriptions;
    private List<LabOrderResponse> labOrders;
}

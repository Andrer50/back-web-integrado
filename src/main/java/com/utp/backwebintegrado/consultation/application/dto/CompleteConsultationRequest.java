package com.utp.backwebintegrado.consultation.application.dto;

import com.utp.backwebintegrado.lab.application.dto.LabOrderRequest;
import lombok.Data;

import java.util.List;

/**
 * Request unificado para finalizar una consulta completa en un solo llamado.
 * El frontend lo envía al hacer clic en "Finalizar Consulta".
 */
@Data
public class CompleteConsultationRequest {
    private String notes;
    private ConsultationVitalsRequest vitals;
    private ConsultationDiagnosisRequest diagnosis;
    private List<ConsultationDiagnosisRequest> diagnoses;
    private PrescriptionRequest prescription;
    private List<LabOrderRequest> labOrders;
    private List<AllergyConsultationRequest> allergies;

    @Data
    public static class AllergyConsultationRequest {
        private String type;
        private String severity;
    }
}

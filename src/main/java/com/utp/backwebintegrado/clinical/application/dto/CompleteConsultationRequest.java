package com.utp.backwebintegrado.clinical.application.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request unificado para finalizar una consulta completa en un solo llamado.
 * El frontend lo envía al hacer clic en "Finalizar Consulta".
 */
@Data
public class CompleteConsultationRequest {
    private String notes;
    private ConsultationVitalsRequest vitals;
    private ConsultationDiagnosisRequest diagnosis;
    private PrescriptionRequest prescription;
    private List<AllergyConsultationRequest> allergies;

    @Data
    public static class AllergyConsultationRequest {
        private String type;
        private String severity;
    }
}

package com.utp.backwebintegrado.clinical.presentation;

import com.utp.backwebintegrado.clinical.application.ConsultationService;
import com.utp.backwebintegrado.clinical.application.dto.*;
import com.utp.backwebintegrado.shared.dto.ApiResponse;
import com.utp.backwebintegrado.shared.utility.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    /** POST /api/v1/consultations — Inicia una consulta vinculada a una cita */
    @PostMapping
    public ResponseEntity<ApiResponse<ConsultationResponse>> create(@RequestBody ConsultationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConsultationResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.createConsultation(request))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> findAll(
            @RequestParam UUID doctorId,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(ApiResponse.<java.util.List<ConsultationResponse>>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.findByDoctorIdAndStatus(doctorId, status))
                .build());
    }

    /** GET /api/v1/consultations/{id} — Obtiene una consulta por su ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.<ConsultationResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.findById(id))
                .build());
    }

    /** GET /api/v1/consultations/appointment/{appointmentId} — Obtiene consulta por ID de cita */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<ConsultationResponse>> findByAppointmentId(@PathVariable UUID appointmentId) {
        return ResponseEntity.ok(ApiResponse.<ConsultationResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.findByAppointmentId(appointmentId))
                .build());
    }

    /** POST /api/v1/consultations/{id}/vitals — Registra signos vitales */
    @PostMapping("/{id}/vitals")
    public ResponseEntity<ApiResponse<ConsultationVitalsResponse>> addVitals(
            @PathVariable UUID id,
            @RequestBody ConsultationVitalsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConsultationVitalsResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.addVitals(id, request))
                .build());
    }

    /** POST /api/v1/consultations/{id}/diagnoses — Agrega un diagnóstico */
    @PostMapping("/{id}/diagnoses")
    public ResponseEntity<ApiResponse<ConsultationDiagnosisResponse>> addDiagnosis(
            @PathVariable UUID id,
            @RequestBody ConsultationDiagnosisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ConsultationDiagnosisResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.addDiagnosis(id, request))
                .build());
    }

    /**
     * POST /api/v1/consultations/{id}/complete
     * Finaliza la consulta completa: notas + vitals + diagnóstico + receta + alergias en un solo request.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ConsultationResponse>> complete(
            @PathVariable UUID id,
            @RequestBody CompleteConsultationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("roles");

        return ResponseEntity.ok(ApiResponse.<ConsultationResponse>builder()
                .code(ConstantUtil.OK_CODE)
                .message(ConstantUtil.OK_MESSAGE)
                .data(consultationService.completeConsultation(id, request, email, roles))
                .build());
    }
}

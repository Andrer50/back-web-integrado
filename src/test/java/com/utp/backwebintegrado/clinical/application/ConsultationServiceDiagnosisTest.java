package com.utp.backwebintegrado.clinical.application;

import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.audit.application.AuditService;
import com.utp.backwebintegrado.consultation.application.dto.CompleteConsultationRequest;
import com.utp.backwebintegrado.consultation.application.dto.ConsultationDiagnosisRequest;
import com.utp.backwebintegrado.consultation.application.dto.ConsultationResponse;
import com.utp.backwebintegrado.consultation.domain.Consultation;
import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosis;
import com.utp.backwebintegrado.consultation.domain.ConsultationDiagnosisRepository;
import com.utp.backwebintegrado.consultation.domain.ConsultationRepository;
import com.utp.backwebintegrado.consultation.domain.ConsultationVitalsRepository;
import com.utp.backwebintegrado.clinical.domain.Diagnosis;
import com.utp.backwebintegrado.clinical.domain.DiagnosisRepository;
import com.utp.backwebintegrado.clinical.domain.MedicationRepository;
import com.utp.backwebintegrado.consultation.domain.PrescriptionRepository;
import com.utp.backwebintegrado.consultation.infrastructure.mapper.ConsultationMapper;
import com.utp.backwebintegrado.consultation.application.ConsultationService;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.lab.infrastructure.LabMapper;
import com.utp.backwebintegrado.patient.domain.AllergyRepository;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.enumeration.DiagnosisType;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceDiagnosisTest {

    @Mock private ConsultationRepository consultationRepository;
    @Mock private ConsultationVitalsRepository vitalsRepository;
    @Mock private ConsultationDiagnosisRepository diagnosisRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private AllergyRepository allergyRepository;
    @Mock private MedicationRepository medicationRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DiagnosisRepository diagnosisEntityRepository;
    @Mock private ConsultationMapper consultationMapper;
    @Mock private LabOrderRepository labOrderRepository;
    @Mock private LabMapper labMapper;
    @Mock private AuditService auditService;

    @InjectMocks
    private ConsultationService consultationService;

    @Test
    void shouldSavePrimaryAndSecondaryDiagnosesWhenCompletingConsultation() {
        UUID consultationId = UUID.randomUUID();
        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .status(AppointmentStatus.CONFIRMED)
                .build();
        Consultation consultation = Consultation.builder()
                .id(consultationId)
                .appointment(appointment)
                .build();
        CompleteConsultationRequest request = new CompleteConsultationRequest();
        request.setDiagnoses(List.of(
                diagnosisRequest("J02.9", "Faringitis aguda", "PRIMARY"),
                diagnosisRequest("R05", "Tos", "SECONDARY")
        ));

        given(consultationRepository.findById(consultationId)).willReturn(Optional.of(consultation));
        given(diagnosisRepository.findByConsultationId(consultationId)).willReturn(List.of());
        given(diagnosisEntityRepository.findByIcd10(any())).willReturn(Optional.empty());
        given(diagnosisEntityRepository.save(any(Diagnosis.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(diagnosisRepository.save(any(ConsultationDiagnosis.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(consultationRepository.save(any(Consultation.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(appointmentRepository.save(any(Appointment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(consultationMapper.toFullResponse(eq(consultation), isNull(), anyList(), isNull()))
                .willReturn(ConsultationResponse.builder().id(consultationId).build());

        consultationService.completeConsultation(
                consultationId,
                request,
                "doctor@mediconnect.pe",
                List.of("DOCTOR")
        );

        ArgumentCaptor<ConsultationDiagnosis> captor = ArgumentCaptor.forClass(ConsultationDiagnosis.class);
        verify(diagnosisRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(ConsultationDiagnosis::getType)
                .containsExactly(DiagnosisType.PRIMARY, DiagnosisType.SECONDARY);

        ArgumentCaptor<Diagnosis> diagnosisCaptor = ArgumentCaptor.forClass(Diagnosis.class);
        verify(diagnosisEntityRepository, org.mockito.Mockito.times(2)).save(diagnosisCaptor.capture());
        assertThat(diagnosisCaptor.getAllValues())
                .extracting(Diagnosis::getIcd10)
                .containsExactly("J02.9", "R05");
    }

    @Test
    void shouldRejectASecondPrimaryDiagnosis() {
        UUID consultationId = UUID.randomUUID();
        Consultation consultation = Consultation.builder().id(consultationId).build();
        ConsultationDiagnosis existingPrimary = ConsultationDiagnosis.builder()
                .type(DiagnosisType.PRIMARY)
                .build();

        given(consultationRepository.findById(consultationId)).willReturn(Optional.of(consultation));
        given(diagnosisRepository.findByConsultationId(consultationId))
                .willReturn(List.of(existingPrimary));

        assertThatThrownBy(() -> consultationService.addDiagnosis(
                consultationId,
                diagnosisRequest("J02.9", "Faringitis aguda", "PRIMARY")
        ))
                .isInstanceOf(ApiValidateException.class)
                .hasMessage("La consulta solo puede tener un diagnóstico principal.");

        verify(diagnosisEntityRepository, never()).findByIcd10(any());
        verify(diagnosisRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateDiagnosisCodeInSameConsultation() {
        UUID consultationId = UUID.randomUUID();
        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .status(AppointmentStatus.CONFIRMED)
                .build();
        Consultation consultation = Consultation.builder()
                .id(consultationId)
                .appointment(appointment)
                .build();
        CompleteConsultationRequest request = new CompleteConsultationRequest();
        request.setDiagnoses(List.of(
                diagnosisRequest("R69", "Primer diagnóstico", "PRIMARY"),
                diagnosisRequest("r69", "Segundo diagnóstico", "SECONDARY")
        ));

        given(consultationRepository.findById(consultationId)).willReturn(Optional.of(consultation));
        given(diagnosisRepository.findByConsultationId(consultationId)).willReturn(List.of());

        assertThatThrownBy(() -> consultationService.completeConsultation(
                consultationId,
                request,
                "doctor@mediconnect.pe",
                List.of("DOCTOR")
        ))
                .isInstanceOf(ApiValidateException.class)
                .hasMessage("El diagnóstico r69 ya está asociado a la consulta.");

        verify(diagnosisEntityRepository, never()).save(any());
        verify(diagnosisRepository, never()).save(any());
    }

    private ConsultationDiagnosisRequest diagnosisRequest(
            String icd10,
            String description,
            String type
    ) {
        ConsultationDiagnosisRequest request = new ConsultationDiagnosisRequest();
        request.setIcd10(icd10);
        request.setDescription(description);
        request.setType(type);
        return request;
    }
}

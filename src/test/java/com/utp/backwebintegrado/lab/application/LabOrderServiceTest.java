package com.utp.backwebintegrado.lab.application;

import com.utp.backwebintegrado.consultation.domain.ConsultationRepository;
import com.utp.backwebintegrado.lab.application.dto.LabResultRequest;
import com.utp.backwebintegrado.lab.application.dto.LabResultResponse;
import com.utp.backwebintegrado.lab.domain.LabOrder;
import com.utp.backwebintegrado.lab.domain.LabOrderRepository;
import com.utp.backwebintegrado.lab.domain.LabResult;
import com.utp.backwebintegrado.lab.domain.LabResultRepository;
import com.utp.backwebintegrado.lab.infrastructure.LabMapper;
import com.utp.backwebintegrado.shared.enumeration.LabOrderStatus;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LabOrderServiceTest {

    @Mock private LabOrderRepository labOrderRepository;
    @Mock private LabResultRepository labResultRepository;
    @Mock private ConsultationRepository consultationRepository;
    @Mock private LabMapper labMapper;

    @InjectMocks
    private LabOrderService labOrderService;

    @Test
    void shouldRecordResultLinkedToOriginalOrder() {
        UUID labOrderId = UUID.randomUUID();
        LabOrder labOrder = LabOrder.builder()
                .id(labOrderId)
                .status(LabOrderStatus.PENDING)
                .build();
        LabResultRequest request = new LabResultRequest();
        request.setDetails("  Hemoglobina: 14 g/dL  ");
        LabResultResponse expected = LabResultResponse.builder()
                .id(UUID.randomUUID())
                .labOrderId(labOrderId)
                .details("Hemoglobina: 14 g/dL")
                .recordedAt(LocalDateTime.now())
                .build();

        given(labOrderRepository.findById(labOrderId)).willReturn(Optional.of(labOrder));
        given(labResultRepository.findByLabOrderId(labOrderId)).willReturn(Optional.empty());
        given(labResultRepository.save(any(LabResult.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(labOrderRepository.save(labOrder)).willReturn(labOrder);
        given(labMapper.toResultResponse(any(LabResult.class))).willReturn(expected);

        LabResultResponse response = labOrderService.recordResult(labOrderId, request);

        ArgumentCaptor<LabResult> resultCaptor = ArgumentCaptor.forClass(LabResult.class);
        verify(labResultRepository).save(resultCaptor.capture());
        assertThat(resultCaptor.getValue().getLabOrder()).isSameAs(labOrder);
        assertThat(resultCaptor.getValue().getDetails()).isEqualTo("Hemoglobina: 14 g/dL");
        assertThat(labOrder.getStatus()).isEqualTo(LabOrderStatus.COMPLETED);
        assertThat(labOrder.getLabResult()).isSameAs(resultCaptor.getValue());
        assertThat(response).isSameAs(expected);
    }

    @Test
    void shouldRejectDuplicateResultForSameOrder() {
        UUID labOrderId = UUID.randomUUID();
        LabOrder labOrder = LabOrder.builder()
                .id(labOrderId)
                .status(LabOrderStatus.COMPLETED)
                .build();
        LabResultRequest request = new LabResultRequest();
        request.setDetails("Nuevo resultado");

        given(labOrderRepository.findById(labOrderId)).willReturn(Optional.of(labOrder));
        given(labResultRepository.findByLabOrderId(labOrderId))
                .willReturn(Optional.of(LabResult.builder().labOrder(labOrder).build()));

        assertThatThrownBy(() -> labOrderService.recordResult(labOrderId, request))
                .isInstanceOf(ApiValidateException.class)
                .hasMessage("La orden de examen ya tiene un resultado registrado.");

        verify(labResultRepository, never()).save(any());
        verify(labOrderRepository, never()).save(any());
    }
}

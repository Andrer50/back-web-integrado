package com.utp.backwebintegrado.patient.application;

import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.patient.infrastructure.PatientMapper;
import com.utp.backwebintegrado.shared.client.AuthClient;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import com.utp.backwebintegrado.shared.exception.ServiceUnavailableException;
import com.utp.backwebintegrado.user.domain.User;
import com.utp.backwebintegrado.user.domain.UserRepository;
import com.utp.backwebintegrado.user.infrastructure.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    // Mocks — simulan las dependencias
    @Mock
    private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private PatientMapper patientMapper;
    @Mock private UserMapper userMapper;
    @Mock private AuthClient authClient;

    // La clase que estamos testeando
    @InjectMocks
    private PatientService patientService;

    // Datos de prueba reutilizables
    private Patient savedPatient;
    private User shadowUser;
    private PatientRequest validRequest;
    private PatientResponse expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = PatientRequest.builder()
                .documentNumber("12345678")
                .email("juan@example.com")
                .password("password123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .phone("999888777")
                .build();

        shadowUser = User.builder()
                .id(UUID.randomUUID())
                .email("juan@example.com")
                .role("PATIENT")
                .status("ACTIVE")
                .build();

        savedPatient = Patient.builder()
                .id(UUID.randomUUID())
                .user(shadowUser)
                .documentNumber("12345678")
                .firstName("Juan")
                .lastName("Pérez")
                .build();

        expectedResponse = PatientResponse.builder()
                .id(savedPatient.getId())
                .documentNumber("12345678")
                .firstName("Juan")
                .lastName("Pérez")
                .build();
    }

    // ─── Agrupa los tests por método ───────────────────────────────────────

    @Nested
    @DisplayName("createPatient()")
    class CreatePatient {

        @Test
        @DisplayName("debe crear paciente exitosamente cuando los datos son válidos")
        void shouldCreatePatientSuccessfully() {
            // ── ARRANGE (Given) ─────────────────────────────────────────────
            // Simula que el documento y email NO existen
            given(patientRepository.existsByDocumentNumber("12345678"))
                    .willReturn(false);
            given(userRepository.existsByEmail("juan@example.com"))
                    .willReturn(false);

            // Simula el mapper
            given(userMapper.toEntity(any(), any(), any()))
                    .willReturn(shadowUser);
            given(patientMapper.toEntity(any(), any()))
                    .willReturn(savedPatient);
            given(patientMapper.toResponse(any()))
                    .willReturn(expectedResponse);

            // Simula el guardado
            given(userRepository.save(any())).willReturn(shadowUser);
            given(patientRepository.save(any())).willReturn(savedPatient);

            // Simula que authClient no falla
            willDoNothing().given(authClient).register(any());

            // ── ACT (When) ──────────────────────────────────────────────────
            PatientResponse response =
                    patientService.createPatient(validRequest);

            // ── ASSERT (Then) ───────────────────────────────────────────────
            assertThat(response).isNotNull();
            assertThat(response.getDocumentNumber()).isEqualTo("12345678");
            assertThat(response.getFirstName()).isEqualTo("Juan");

            // Verifica que se llamó al authClient exactamente 1 vez
            then(authClient).should(times(1)).register(any());

            // Verifica que se guardó el usuario y el paciente
            then(userRepository).should(times(1)).save(any());
            then(patientRepository).should(times(1)).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción cuando el documento ya existe")
        void shouldThrowWhenDocumentAlreadyExists() {
            // ── ARRANGE ─────────────────────────────────────────────────────
            given(patientRepository.existsByDocumentNumber("12345678"))
                    .willReturn(true); // documento duplicado

            // ── ACT & ASSERT ─────────────────────────────────────────────────
            assertThatThrownBy(() -> patientService.createPatient(validRequest))
                    .isInstanceOf(ApiValidateException.class)
                    .hasMessage("El documento ya está registrado.");

            // Verifica que NO se llamó a nada más
            then(authClient).shouldHaveNoInteractions();
            then(userRepository).should(never()).save(any());
            then(patientRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción cuando el email ya existe")
        void shouldThrowWhenEmailAlreadyExists() {
            // ── ARRANGE ─────────────────────────────────────────────────────
            given(patientRepository.existsByDocumentNumber("12345678"))
                    .willReturn(false);
            given(userRepository.existsByEmail("juan@example.com"))
                    .willReturn(true); // email duplicado

            // ── ACT & ASSERT ─────────────────────────────────────────────────
            assertThatThrownBy(() -> patientService.createPatient(validRequest))
                    .isInstanceOf(ApiValidateException.class)
                    .hasMessage("El correo ya está registrado.");

            then(authClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("debe lanzar excepción cuando auth-service falla")
        void shouldThrowWhenAuthServiceFails() {
            // ── ARRANGE ─────────────────────────────────────────────────────
            given(patientRepository.existsByDocumentNumber(any()))
                    .willReturn(false);
            given(userRepository.existsByEmail(any()))
                    .willReturn(false);
            given(userMapper.toEntity(any(), any(),any()))
                    .willReturn(shadowUser);
            given(patientMapper.toEntity(any(), any()))
                    .willReturn(savedPatient);
            given(userRepository.save(any())).willReturn(shadowUser);
            given(patientRepository.save(any())).willReturn(savedPatient);

            // Simula que authClient lanza excepción
            willThrow(new ServiceUnavailableException("Auth no disponible"))
                    .given(authClient).register(any());

            // ── ACT & ASSERT ─────────────────────────────────────────────────
            assertThatThrownBy(() -> patientService.createPatient(validRequest))
                    .isInstanceOf(ServiceUnavailableException.class)
                    .hasMessage("Auth no disponible");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("debe retornar paciente cuando existe")
        void shouldReturnPatientWhenExists() {
            UUID id = savedPatient.getId();
            given(patientRepository.findById(id))
                    .willReturn(Optional.of(savedPatient));
            given(patientMapper.toResponse(savedPatient))
                    .willReturn(expectedResponse);

            PatientResponse response = patientService.findById(id);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("debe lanzar excepción cuando paciente no existe")
        void shouldThrowWhenPatientNotFound() {
            UUID id = UUID.randomUUID();
            given(patientRepository.findById(id))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> patientService.findById(id))
                    .isInstanceOf(ApiValidateException.class)
                    .hasMessage("Paciente no encontrado");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("debe retornar lista de pacientes")
        void shouldReturnAllPatients() {
            given(patientRepository.findAll())
                    .willReturn(List.of(savedPatient));
            given(patientMapper.toResponse(savedPatient))
                    .willReturn(expectedResponse);

            List<PatientResponse> responses = patientService.findAll();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getDocumentNumber())
                    .isEqualTo("12345678");
        }

        @Test
        @DisplayName("debe retornar lista vacía cuando no hay pacientes")
        void shouldReturnEmptyList() {
            given(patientRepository.findAll()).willReturn(List.of());

            List<PatientResponse> responses = patientService.findAll();

            assertThat(responses).isEmpty();
        }
    }
}
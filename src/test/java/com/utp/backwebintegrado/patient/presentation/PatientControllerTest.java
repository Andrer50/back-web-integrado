package com.utp.backwebintegrado.patient.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.backwebintegrado.patient.application.PatientService;
import com.utp.backwebintegrado.patient.application.dto.PatientRequest;
import com.utp.backwebintegrado.patient.application.dto.PatientResponse;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PatientService patientService;

    @Test
    @DisplayName("POST /patients debe retornar 201 con datos válidos")
    void shouldReturn201WhenCreatePatient() throws Exception {

        PatientRequest request = PatientRequest.builder()
                .documentNumber("12345678")
                .email("juan@example.com")
                .password("password123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

        PatientResponse response = PatientResponse.builder()
                .id(UUID.randomUUID())
                .documentNumber("12345678")
                .firstName("Juan")
                .lastName("Pérez")
                .build();

        given(patientService.createPatient(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.documentNumber").value("12345678"))
                .andExpect(jsonPath("$.data.firstName").value("Juan"));
    }

    @Test
    @DisplayName("POST /patients debe retornar 400 cuando documento duplicado")
    void shouldReturn400WhenDocumentExists() throws Exception {

        PatientRequest request = PatientRequest.builder()
                .documentNumber("12345678")
                .email("juan@example.com")
                .password("password123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();

        given(patientService.createPatient(any()))
                .willThrow(new ApiValidateException("El documento ya está registrado."));

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("El documento ya está registrado."));
    }

    @Test
    @DisplayName("POST /patients debe retornar 400 cuando faltan campos")
    void shouldReturn400WhenMissingFields() throws Exception {

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
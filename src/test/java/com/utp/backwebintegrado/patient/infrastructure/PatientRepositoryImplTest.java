package com.utp.backwebintegrado.patient.infrastructure;

import com.utp.backwebintegrado.patient.domain.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // levanta solo JPA, sin todo el contexto Spring
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PatientRepositoryImplTest {

    @Autowired
    private PatientJpaRepository jpaRepository;
    private PatientRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PatientRepositoryImpl(jpaRepository);
    }

    @Test
    @DisplayName("existsByDocumentNumber debe retornar true cuando existe")
    void shouldReturnTrueWhenDocumentExists() {
        Patient patient = Patient.builder()
                .id(UUID.randomUUID())
                .documentNumber("12345678")
                .firstName("Juan")
                .lastName("Pérez")
                .build();
        jpaRepository.save(patient);

        boolean exists = repository.existsByDocumentNumber("12345678");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByDocumentNumber debe retornar false cuando no existe")
    void shouldReturnFalseWhenDocumentNotExists() {
        boolean exists = repository.existsByDocumentNumber("99999999");

        assertThat(exists).isFalse();
    }
}
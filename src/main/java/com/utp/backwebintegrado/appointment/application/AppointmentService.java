package com.utp.backwebintegrado.appointment.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentRequest;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.appointment.infrastructure.mapper.AppointmentMapper;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;
import com.utp.backwebintegrado.clinical.domain.Consultation;
import com.utp.backwebintegrado.clinical.domain.ConsultationRepository;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;
    private final ConsultationRepository consultationRepository;

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ApiValidateException("Paciente no encontrado con ID: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + request.getDoctorId()));

        Appointment appointment = Appointment.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.CONFIRMED) // Por defecto confirmada para demostración
                .reason(request.getReason())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // Generar automáticamente la consulta médica asociada en estado PENDING
        Consultation consultation = Consultation.builder()
                .appointment(saved)
                .notes("")
                .status(ConsultationStatus.PENDING)
                .build();
        consultationRepository.save(consultation);

        return appointmentMapper.toResponse(saved);
    }

    public Page<AppointmentResponse> findAllPaginated(UUID patientId, UUID doctorId, String status, Pageable pageable) {
        AppointmentStatus appointmentStatus = null;
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            try {
                appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si el status enviado no coincide con el Enum, se ignora o maneja
            }
        }
        return appointmentRepository.findAll(patientId, doctorId, appointmentStatus, pageable)
                .map(appointmentMapper::toResponse);
    }
}

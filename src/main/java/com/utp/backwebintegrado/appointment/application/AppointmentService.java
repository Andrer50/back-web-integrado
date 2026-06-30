package com.utp.backwebintegrado.appointment.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentRequest;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
import com.utp.backwebintegrado.appointment.domain.Appointment;
import com.utp.backwebintegrado.appointment.domain.AppointmentRepository;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlotRepository;
import com.utp.backwebintegrado.appointment.infrastructure.mapper.AppointmentMapper;
import com.utp.backwebintegrado.audit.application.AuditService;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import com.utp.backwebintegrado.patient.domain.Patient;
import com.utp.backwebintegrado.patient.domain.PatientRepository;
import com.utp.backwebintegrado.shared.enumeration.AppointmentStatus;
import com.utp.backwebintegrado.shared.enumeration.ConsultationStatus;
import com.utp.backwebintegrado.shared.enumeration.SlotStatus;
import com.utp.backwebintegrado.consultation.domain.Consultation;
import com.utp.backwebintegrado.consultation.domain.ConsultationRepository;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;
    private final ConsultationRepository consultationRepository;
    private final DoctorScheduleSlotRepository slotRepository;
    private final AuditService auditService;


    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse createAppointment(AppointmentRequest request, String userEmail, List<String> roles) {

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new ApiValidateException("El motivo de la consulta es obligatorio para agendar la cita.");
        }

        boolean isAdmin = roles != null && roles.contains("ADMIN");

        Patient patient;
        if (isAdmin) {
            // El admin debe especificar para qué paciente se agenda la cita
            if (request.getPatientId() == null) {
                throw new ApiValidateException("Debes indicar el paciente (patientId) para agendar la cita.");
            }
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ApiValidateException("No se encontró el paciente con ID: " + request.getPatientId()));
        } else {
            // El paciente agenda su propia cita a partir de su token
            patient = patientRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new ApiValidateException("No se encontró un paciente asociado a este usuario."));
        }

        DoctorScheduleSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ApiValidateException("Horario no encontrado con ID: " + request.getSlotId()));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new ApiValidateException("El horario seleccionado ya no está disponible. Por favor elige otro.");
        }

        Doctor doctor = slot.getDoctor();

        Appointment appointment = Appointment.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(slot.getSlotDate())
                .appointmentTime(slot.getStartTime())
                .status(AppointmentStatus.PENDING)
                .reason(request.getReason())
                .scheduleSlot(slot)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        slot.setStatus(SlotStatus.BOOKED);
        slot.setAppointment(saved);
        slotRepository.save(slot);

        Consultation consultation = Consultation.builder()
                .appointment(saved)
                .notes("")
                .status(ConsultationStatus.PENDING)
                .build();
        consultationRepository.save(consultation);
        auditService.recordAppointmentCreated(saved, userEmail, roles);

        return appointmentMapper.toResponse(saved);
    }

    /**
     * Lista citas aplicando reglas de visibilidad por rol:
     * - ADMIN: ve todas las citas, opcionalmente filtradas por patientId/doctorId/status que envíe.
     * - DOCTOR: solo ve sus propias citas (se ignora cualquier doctorId enviado por el cliente).
     * - PATIENT: solo ve sus propias citas (se ignora cualquier patientId enviado por el cliente).
     */
    public Page<AppointmentResponse> findAllPaginated(
            UUID patientIdParam,
            UUID doctorIdParam,
            String status,
            Pageable pageable,
            List<String> roles,
            String userEmail) {

        AppointmentStatus appointmentStatus = null;
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            try {
                appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si el status enviado no coincide con el Enum, se ignora
            }
        }

        boolean isAdmin = roles != null && roles.contains("ADMIN");
        boolean isDoctor = roles != null && roles.contains("DOCTOR");
        boolean isPatient = roles != null && roles.contains("PATIENT");

        UUID effectivePatientId = patientIdParam;
        UUID effectiveDoctorId = doctorIdParam;

        if (isAdmin) {
            // Admin: respeta los filtros que mande (o ninguno = todas)
        } else if (isDoctor) {
            Doctor doctor = doctorRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new ApiValidateException("No se encontró un doctor asociado a este usuario."));
            effectiveDoctorId = doctor.getId();
            effectivePatientId = null; // un doctor no filtra por paciente arbitrario, solo ve las suyas
        } else if (isPatient) {
            Patient patient = patientRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new ApiValidateException("No se encontró un paciente asociado a este usuario."));
            effectivePatientId = patient.getId();
            effectiveDoctorId = null;
        } else {
            throw new ApiValidateException("No tienes permiso para listar citas.");
        }

        return appointmentRepository.findAll(effectivePatientId, effectiveDoctorId, appointmentStatus, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse changeStatus(UUID appointmentId, String newStatus, List<String> roles, String userEmail) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ApiValidateException("Cita no encontrada."));

        AppointmentStatus current = appointment.getStatus();
        AppointmentStatus target;

        try {
            target = AppointmentStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiValidateException("Estado inválido: " + newStatus);
        }

        boolean isPatient = roles.contains("PATIENT");
        boolean isDoctor  = roles.contains("DOCTOR");
        boolean isAdmin   = roles.contains("ADMIN");

        if (isPatient) {
            if (target != AppointmentStatus.CANCELLED) {
                throw new ApiValidateException("Como paciente solo puedes cancelar una cita.");
            }
            if (current != AppointmentStatus.PENDING) {
                throw new ApiValidateException("Solo puedes cancelar una cita que esté pendiente.");
            }
            if (!appointment.getPatient().getUser().getEmail().equals(userEmail)) {
                throw new ApiValidateException("No tienes permiso para modificar esta cita.");
            }
        } else if (isDoctor || isAdmin) {
            boolean validTransition =
                    (current == AppointmentStatus.PENDING   && target == AppointmentStatus.CONFIRMED) ||
                            (current == AppointmentStatus.CONFIRMED && target == AppointmentStatus.COMPLETED) ||
                            (current == AppointmentStatus.PENDING   && target == AppointmentStatus.CANCELLED) ||
                            (current == AppointmentStatus.CONFIRMED && target == AppointmentStatus.CANCELLED);

            if (!validTransition) {
                throw new ApiValidateException(
                        "Transición no permitida: " + current + " → " + target
                );
            }
        } else {
            throw new ApiValidateException("No tienes permiso para cambiar el estado de esta cita.");
        }

        appointment.setStatus(target);
        appointmentRepository.save(appointment);

        if (target == AppointmentStatus.CANCELLED) {
            DoctorScheduleSlot slot = appointment.getScheduleSlot();
            if (slot != null) {
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setAppointment(null);
                slotRepository.save(slot);
            }
        }

        auditService.recordAppointmentStatusChange(appointment, current, target, userEmail, roles);

        return appointmentMapper.toResponse(appointment);
    }
}

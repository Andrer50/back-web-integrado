package com.utp.backwebintegrado.appointment.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.appointment.application.dto.AvailableDoctorSlotsResponse;
import com.utp.backwebintegrado.appointment.application.dto.DoctorScheduleSlotResponse;
import com.utp.backwebintegrado.appointment.application.dto.GenerateSlotsRequest;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlotRepository;
import com.utp.backwebintegrado.appointment.infrastructure.mapper.DoctorScheduleSlotMapper;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoomRepository;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import com.utp.backwebintegrado.shared.enumeration.SlotStatus;
import com.utp.backwebintegrado.shared.exception.ApiValidateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DoctorScheduleSlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final ConsultingRoomRepository roomRepository;
    private final DoctorScheduleSlotMapper slotMapper;

    /**
     * Genera automáticamente las ranuras de tiempo de atención para un médico en un consultorio físico y fecha específicos.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<DoctorScheduleSlotResponse> generateSlots(GenerateSlotsRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + request.getDoctorId()));

        ConsultingRoom room = roomRepository.findById(request.getConsultingRoomId())
                .orElseThrow(() -> new ApiValidateException("Consultorio no encontrado con ID: " + request.getConsultingRoomId()));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new ApiValidateException("La hora de inicio no puede ser posterior a la de fin");
        }

        List<DoctorScheduleSlot> slotsToCreate = new ArrayList<>();
        LocalTime current = request.getStartTime();
        int duration = request.getSlotDurationMinutes();

        while (current.plusMinutes(duration).isBefore(request.getEndTime()) || current.plusMinutes(duration).equals(request.getEndTime())) {
            LocalTime next = current.plusMinutes(duration);

            DoctorScheduleSlot slot = DoctorScheduleSlot.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .doctor(doctor)
                    .consultingRoom(room)
                    .slotDate(request.getDate())
                    .startTime(current)
                    .endTime(next)
                    .status(SlotStatus.AVAILABLE)
                    .build();

            slotsToCreate.add(slot);
            current = next;
        }

        if (slotsToCreate.isEmpty()) {
            throw new ApiValidateException("El intervalo de tiempo ingresado es demasiado corto para la duración del slot");
        }

        try {
            List<DoctorScheduleSlot> saved = slotRepository.saveAll(slotsToCreate);
            return saved.stream().map(slotMapper::toResponse).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApiValidateException("Error de programación: conflicto de horarios o uso del consultorio seleccionado.");
        }
    }

    /**
     * Busca ranuras disponibles filtradas por especialidad, sede y rango de fechas, agrupándolas con la estructura de Clinica Aviva.
     */
    public List<AvailableDoctorSlotsResponse> findAvailableSlotsGrouped(UUID specialtyId, UUID branchId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now();
        LocalDate end = endDate != null ? endDate : start.plusDays(14); // Por defecto busca las próximas 2 semanas

        // Obtener ranuras libres en la base de datos
        List<DoctorScheduleSlot> rawSlots = slotRepository.findAvailableSlots(specialtyId, branchId, start, end);

        // Agrupar primero por Doctor
        Map<Doctor, List<DoctorScheduleSlot>> slotsByDoctor = rawSlots.stream()
                .collect(Collectors.groupingBy(DoctorScheduleSlot::getDoctor));

        List<AvailableDoctorSlotsResponse> responseList = new ArrayList<>();

        for (Map.Entry<Doctor, List<DoctorScheduleSlot>> doctorEntry : slotsByDoctor.entrySet()) {
            Doctor doc = doctorEntry.getKey();
            List<DoctorScheduleSlot> docSlots = doctorEntry.getValue();

            // Sede de atención (tomamos la primera disponible en sus slots asignados)
            ConsultingRoom sampleRoom = docSlots.get(0).getConsultingRoom();
            String branchName = sampleRoom.getBranch().getName();
            String branchAddress = sampleRoom.getBranch().getAddress();
            String specialtyName = doc.getSpecialties().isEmpty() ? "General" : doc.getSpecialties().iterator().next().getName();

            // Agrupar ranuras del médico por fecha (slotDate)
            Map<LocalDate, List<DoctorScheduleSlot>> slotsByDate = docSlots.stream()
                    .collect(Collectors.groupingBy(DoctorScheduleSlot::getSlotDate, TreeMap::new, Collectors.toList()));

            List<AvailableDoctorSlotsResponse.DateGroup> dateGroups = new ArrayList<>();

            for (Map.Entry<LocalDate, List<DoctorScheduleSlot>> dateEntry : slotsByDate.entrySet()) {
                LocalDate date = dateEntry.getKey();
                List<DoctorScheduleSlot> dateSlots = dateEntry.getValue();

                // Formatear etiquetas de fecha en español
                String dayLabel = capitalize(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "PE"))).replace(".", "");
                String dateLabel = date.getDayOfMonth() + " " + capitalize(date.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "PE"))).replace(".", "");

                List<AvailableDoctorSlotsResponse.SlotItem> slotItems = dateSlots.stream()
                        .map(s -> AvailableDoctorSlotsResponse.SlotItem.builder()
                                .slotId(s.getId())
                                .time(s.getStartTime().toString())
                                .build())
                        .collect(Collectors.toList());

                dateGroups.add(AvailableDoctorSlotsResponse.DateGroup.builder()
                        .date(date.toString())
                        .dayLabel(dayLabel)
                        .dateLabel(dateLabel)
                        .slots(slotItems)
                        .build());
            }

            responseList.add(AvailableDoctorSlotsResponse.builder()
                    .doctorId(doc.getId())
                    .doctorName("Dr. " + doc.getFirstName() + " " + doc.getLastName())
                    .cmp(doc.getMedicalLicenseNumber())
                    .specialty(specialtyName)
                    .branchName(branchName)
                    .branchAddress(branchAddress)
                    .modality("PRESENCIAL") // Modulación estándar
                    .availableDates(dateGroups)
                    .build());
        }

        return responseList;
    }

    public List<DoctorScheduleSlotResponse> findSlotsByDoctor(UUID doctorId, LocalDate startDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(1); // Muestra slots de hoy también
        List<DoctorScheduleSlot> slots = slotRepository.findByDoctorIdAndStartDate(doctorId, start);
        return slots.stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}

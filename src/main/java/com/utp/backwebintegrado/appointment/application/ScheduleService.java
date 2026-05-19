package com.utp.backwebintegrado.appointment.application;

import com.github.f4b6a3.uuid.UuidCreator;
import com.utp.backwebintegrado.appointment.application.dto.AvailableDoctorSlotsResponse;
import com.utp.backwebintegrado.appointment.application.dto.DoctorScheduleSlotResponse;
import com.utp.backwebintegrado.appointment.application.dto.GenerateSlotsRequest;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlot;
import com.utp.backwebintegrado.appointment.domain.DoctorScheduleSlotRepository;
import com.utp.backwebintegrado.appointment.infrastructure.mapper.DoctorScheduleSlotMapper;
import com.utp.backwebintegrado.appointment.infrastructure.mapper.AppointmentMapper;
import com.utp.backwebintegrado.appointment.application.dto.AppointmentResponse;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoom;
import com.utp.backwebintegrado.clinical.domain.ConsultingRoomRepository;
import com.utp.backwebintegrado.doctor.domain.Doctor;
import com.utp.backwebintegrado.doctor.domain.DoctorRepository;
import com.utp.backwebintegrado.doctor.domain.DoctorSchedule;
import com.utp.backwebintegrado.doctor.domain.DoctorScheduleRepository;
import com.utp.backwebintegrado.doctor.domain.DoctorOffDay;
import com.utp.backwebintegrado.doctor.domain.DoctorOffDayRepository;
import com.utp.backwebintegrado.doctor.application.dto.DoctorScheduleRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorScheduleResponse;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDayRequest;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDayResponse;
import com.utp.backwebintegrado.doctor.application.dto.DoctorOffDaySaveResponse;
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
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorOffDayRepository offDayRepository;
    private final AppointmentMapper appointmentMapper;


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

    // --- NUEVOS MÉTODOS DE CONFIGURACIÓN SEMANAL ---

    public List<DoctorScheduleResponse> getWeeklyConfigs(UUID doctorId) {
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        return schedules.stream()
                .map(this::mapToWeeklyConfigResponse)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DoctorScheduleResponse> saveWeeklyConfigs(UUID doctorId, List<DoctorScheduleRequest> requests) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + doctorId));

        // Obtener configuraciones existentes y eliminarlas
        List<DoctorSchedule> existing = scheduleRepository.findByDoctorId(doctorId);
        for (DoctorSchedule schedule : existing) {
            scheduleRepository.delete(schedule);
        }

        List<DoctorSchedule> toSave = new ArrayList<>();
        for (DoctorScheduleRequest req : requests) {
            ConsultingRoom room = roomRepository.findById(req.getConsultingRoomId())
                    .orElseThrow(() -> new ApiValidateException("Consultorio no encontrado con ID: " + req.getConsultingRoomId()));

            DoctorSchedule schedule = DoctorSchedule.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .doctor(doctor)
                    .dayOfWeek(req.getDayOfWeek().toUpperCase())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .consultingRoom(room)
                    .slotDurationMinutes(req.getSlotDurationMinutes())
                    .isActive(req.isActive())
                    .build();

            toSave.add(schedule);
        }

        List<DoctorSchedule> saved = scheduleRepository.saveAll(toSave);

        // Generar/completar slots para los próximos 7 días si es que no hay slots aún en esas fechas
        for (int i = 0; i <= 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            if (!slotRepository.existsByDoctorIdAndSlotDate(doctorId, date)) {
                if (!offDayRepository.existsByDoctorIdAndOffDate(doctorId, date)) {
                    generateSlotsForDoctorAndDate(doctor, date);
                }
            }
        }

        return saved.stream()
                .map(this::mapToWeeklyConfigResponse)
                .collect(Collectors.toList());
    }

    // --- NUEVOS MÉTODOS DE DÍAS LIBRES (OFF DAYS) ---

    public List<DoctorOffDayResponse> getOffDays(UUID doctorId) {
        List<DoctorOffDay> offDays = offDayRepository.findByDoctorIdAndOffDateGreaterThanEqual(doctorId, LocalDate.now());
        return offDays.stream()
                .map(this::mapToOffDayResponse)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public DoctorOffDaySaveResponse saveOffDay(UUID doctorId, DoctorOffDayRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ApiValidateException("Médico no encontrado con ID: " + doctorId));

        if (offDayRepository.existsByDoctorIdAndOffDate(doctorId, request.getOffDate())) {
            throw new ApiValidateException("El médico ya tiene registrado el día libre en la fecha: " + request.getOffDate());
        }

        DoctorOffDay offDay = DoctorOffDay.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .doctor(doctor)
                .offDate(request.getOffDate())
                .reason(request.getReason())
                .build();

        DoctorOffDay saved = offDayRepository.save(offDay);

        // Buscar todos los slots generados para esta fecha
        List<DoctorScheduleSlot> existingSlots = slotRepository.findByDoctorIdAndSlotDate(doctorId, request.getOffDate());
        List<DoctorScheduleSlot> slotsToDelete = new ArrayList<>();
        List<AppointmentResponse> conflicts = new ArrayList<>();

        for (DoctorScheduleSlot slot : existingSlots) {
            if (slot.getStatus() == SlotStatus.AVAILABLE) {
                slotsToDelete.add(slot);
            } else if (slot.getStatus() == SlotStatus.BOOKED && slot.getAppointment() != null) {
                conflicts.add(appointmentMapper.toResponse(slot.getAppointment()));
            }
        }

        // Eliminar los slots disponibles
        if (!slotsToDelete.isEmpty()) {
            slotRepository.deleteAll(slotsToDelete);
        }

        return DoctorOffDaySaveResponse.builder()
                .offDay(mapToOffDayResponse(saved))
                .conflicts(conflicts)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOffDay(UUID offDayId) {
        DoctorOffDay offDay = offDayRepository.findById(offDayId)
                .orElseThrow(() -> new ApiValidateException("Día libre no encontrado con ID: " + offDayId));

        offDayRepository.delete(offDay);

        // Si la fecha del día libre borrado está a futuro (a partir de hoy) y dentro de la ventana de 7 días, re-generar los slots
        LocalDate today = LocalDate.now();
        LocalDate endWindow = LocalDate.now().plusDays(7);
        LocalDate offDate = offDay.getOffDate();

        if ((offDate.isAfter(today) || offDate.equals(today)) && (offDate.isBefore(endWindow) || offDate.equals(endWindow))) {
            generateSlotsForDoctorAndDate(offDay.getDoctor(), offDate);
        }
    }

    // --- MÉTODOS DE GENERACIÓN AUTOMÁTICA DE SLOTS ---

    @Transactional(rollbackFor = Exception.class)
    public void generateSlotsForDate(LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().name(); // MONDAY, TUESDAY, etc.
        List<DoctorSchedule> configs = scheduleRepository.findByDayOfWeekAndIsActive(dayOfWeek, true);

        for (DoctorSchedule config : configs) {
            Doctor doctor = config.getDoctor();

            // Omitir si el doctor tiene registrado un día libre en esa fecha
            if (offDayRepository.existsByDoctorIdAndOffDate(doctor.getId(), date)) {
                continue;
            }

            // Omitir si ya existen slots para ese doctor en esa fecha
            if (slotRepository.existsByDoctorIdAndSlotDate(doctor.getId(), date)) {
                continue;
            }

            generateSlotsForConfigAndDate(config, date);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateSlotsForDoctorAndDate(Doctor doctor, LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().name();
        List<DoctorSchedule> configs = scheduleRepository.findByDoctorIdAndIsActive(doctor.getId(), true);

        for (DoctorSchedule config : configs) {
            if (config.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                // Omitir si ya existen slots para evitar duplicados
                if (slotRepository.existsByDoctorIdAndSlotDate(doctor.getId(), date)) {
                    continue;
                }
                generateSlotsForConfigAndDate(config, date);
            }
        }
    }

    private void generateSlotsForConfigAndDate(DoctorSchedule config, LocalDate date) {
        List<DoctorScheduleSlot> slotsToCreate = new ArrayList<>();
        LocalTime current = config.getStartTime();
        int duration = config.getSlotDurationMinutes();

        while (current.plusMinutes(duration).isBefore(config.getEndTime()) || current.plusMinutes(duration).equals(config.getEndTime())) {
            LocalTime next = current.plusMinutes(duration);

            DoctorScheduleSlot slot = DoctorScheduleSlot.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .doctor(config.getDoctor())
                    .consultingRoom(config.getConsultingRoom())
                    .slotDate(date)
                    .startTime(current)
                    .endTime(next)
                    .status(SlotStatus.AVAILABLE)
                    .build();

            slotsToCreate.add(slot);
            current = next;
        }

        if (!slotsToCreate.isEmpty()) {
            slotRepository.saveAll(slotsToCreate);
        }
    }

    // --- MAPPERS AUXILIARES ---

    private DoctorScheduleResponse mapToWeeklyConfigResponse(DoctorSchedule schedule) {
        return DoctorScheduleResponse.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctor().getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .consultingRoomId(schedule.getConsultingRoom().getId())
                .consultingRoomNumber(schedule.getConsultingRoom().getRoomNumber())
                .branchName(schedule.getConsultingRoom().getBranch().getName())
                .slotDurationMinutes(schedule.getSlotDurationMinutes())
                .isActive(schedule.isActive())
                .build();
    }

    private DoctorOffDayResponse mapToOffDayResponse(DoctorOffDay offDay) {
        return DoctorOffDayResponse.builder()
                .id(offDay.getId())
                .doctorId(offDay.getDoctor().getId())
                .offDate(offDay.getOffDate())
                .reason(offDay.getReason())
                .build();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}


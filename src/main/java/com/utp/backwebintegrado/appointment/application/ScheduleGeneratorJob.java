package com.utp.backwebintegrado.appointment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleGeneratorJob {

    private final ScheduleService scheduleService;

    /**
     * Corre a la 1:00 AM todos los días para generar los slots del día 8 (hoy + 7 días).
     * Esto mantiene la ventana rodante de 7 días activa y llena.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailySlots() {
        LocalDate targetDate = LocalDate.now().plusDays(7);
        log.info("Iniciando generación automática diaria de slots para la fecha: {}", targetDate);
        try {
            scheduleService.generateSlotsForDate(targetDate);
            log.info("Generación automática completada exitosamente para la fecha: {}", targetDate);
        } catch (Exception e) {
            log.error("Error al generar automáticamente los slots para la fecha {}", targetDate, e);
        }
    }

    /**
     * Al iniciar la aplicación, se asegura de rellenar/backfillear los slots
     * para el día actual y los siguientes 7 días si es que no están generados.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void backfillMissingSlots() {
        log.info("Ejecutando proceso de backfill para asegurar ranuras de los próximos 7 días...");
        for (int i = 0; i <= 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            try {
                scheduleService.generateSlotsForDate(date);
            } catch (Exception e) {
                log.error("Error en backfill de slots para la fecha: {}", date, e);
            }
        }
        log.info("Proceso de backfill finalizado.");
    }
}

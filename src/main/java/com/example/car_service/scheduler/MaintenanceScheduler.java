package com.example.car_service.scheduler;

import com.example.car_service.repository.MaintenanceRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor // Crea il costruttore per iniettare il repository
@Slf4j // Per stampare i log in modo professionale
public class MaintenanceScheduler {

    private final MaintenanceRepository maintenanceRepository;
    private final JavaMailSender mailSender;

    @Value("${maintenance.monitor.enabled}")
    private boolean isMonitorEnabled;

    @Value("${maintenance.email.enabled}")
    private boolean isEmailEnabled;

    // Esegue il compito ogni tot secondi come definito in application.properties
    @Scheduled(fixedRateString = "${maintenance.monitor.rate}")
    public void monitorMaintenances() {
        if (!isMonitorEnabled) {
            return; // Esce subito e non manda nulla
        }
        long count = maintenanceRepository.count();
        log.info("--- REPORT AUTOMATICO ---");
        log.info("Totale manutenzioni a sistema: {}", count);
        log.info("--------------------------");
    }

    @Scheduled(fixedRate = 30000)
    //@Scheduled(cron = "0 0 12 * * ?") // Ogni giorno a mezzogiorno
    public void reportDiMezzogiorno() {
        if (!isEmailEnabled) {
            return; // Esce subito e non manda nulla
        }
        log.info("Generazione report giornaliero...");

        // 1. Recuperiamo il dato dal database
        long count = maintenanceRepository.count();

        // 2. Creiamo il messaggio
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("service@car-service.com"); // Può essere un'email fittizia
        message.setTo("test@example.com");          // Tanto finirà nella tua inbox di Mailtrap
        message.setSubject("Car Service - Report Giornaliero");
        message.setText("Il task schedulato è attivo. Manutenzioni totali: " + count);

        // 3. Spediamo la mail (IL PEZZO MANCANTE)
        try {
            mailSender.send(message); // <-- Questo è il comando che "preme il tasto invio"
            log.info("Email inviata con successo!");
        } catch (Exception e) {
            log.error("Errore durante l'invio della mail: {}", e.getMessage());
        }
    }
}

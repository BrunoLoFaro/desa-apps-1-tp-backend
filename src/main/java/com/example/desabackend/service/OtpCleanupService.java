package com.example.desabackend.service;

import com.example.desabackend.repository.OtpRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Limpia periódicamente OTPs viejos para evitar que la tabla crezca indefinidamente.
 * Se ejecuta todos los días a las 3 AM.
 */
@Service
public class OtpCleanupService {

    private static final Logger log = LoggerFactory.getLogger(OtpCleanupService.class);
    private static final int RETENTION_HOURS = 24;

    private final OtpRepository otpRepository;

    public OtpCleanupService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(RETENTION_HOURS);
        int deleted = otpRepository.deleteByCreatedAtBefore(cutoff);
        log.info("OTP cleanup: {} registros eliminados (anteriores a {}h)", deleted, RETENTION_HOURS);
    }
}

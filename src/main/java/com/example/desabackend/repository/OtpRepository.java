package com.example.desabackend.repository;

import com.example.desabackend.entity.OtpEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    // Busca el OTP activo más reciente (no verificado y no expirado)
    Optional<OtpEntity> findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, LocalDateTime now);

    // FIX: solo buscar no-verificados para resend (antes era sin filtro verified)
    Optional<OtpEntity> findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(String email);

    // Para limpieza periódica de OTPs viejos
    @Modifying
    int deleteByCreatedAtBefore(LocalDateTime cutoff);
}

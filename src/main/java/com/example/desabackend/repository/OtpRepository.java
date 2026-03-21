package com.example.desabackend.repository;

import com.example.desabackend.entity.OtpEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email,
            LocalDateTime now
    );

    Optional<OtpEntity> findTopByEmailOrderByCreatedAtDesc(String email);
}

package com.example.desabackend.repository;

import com.example.desabackend.entity.OtpEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    @Query("SELECT o FROM OtpEntity o WHERE o.email = :email AND o.verified = false AND o.expiresAt > :now ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpEntity> findLatestActiveOtpByEmail(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT o FROM OtpEntity o WHERE o.email = :email ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpEntity> findLatestOtpByEmail(@Param("email") String email);
}

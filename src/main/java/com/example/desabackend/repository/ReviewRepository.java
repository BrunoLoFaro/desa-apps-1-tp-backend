package com.example.desabackend.repository;

import com.example.desabackend.entity.ReviewEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByBookingId(Long bookingId);

    Optional<ReviewEntity> findByBookingId(Long bookingId);

    Optional<ReviewEntity> findByBookingIdAndUserId(Long bookingId, Long userId);
}

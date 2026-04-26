package com.example.desabackend.dto;

import com.example.desabackend.entity.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        Long sessionId,
        Long activityId,
        String activityName,
        DestinationDto destination,
        String guideName,
        LocalDateTime sessionStartTime,
        int durationMinutes,
        int participants,
        BigDecimal totalPrice,
        String currency,
        BookingStatus status,
        String cancellationPolicy,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt,
        boolean canReview,
        String voucherCode
) {
}

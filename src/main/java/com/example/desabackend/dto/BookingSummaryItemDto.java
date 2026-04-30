package com.example.desabackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingSummaryItemDto(
        Long id,
        Long activityId,
        String activityName,
        String status,
        LocalDateTime sessionStartTime,
        BigDecimal totalPrice,
        String currency,
        String destination,
        String guideName,
        Integer durationMinutes,
        String imageUrl,
        boolean canReview
) {
}

package com.example.desabackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoryItemDto(
        Long bookingId,
        Long activityId,
        String activityName,
        DestinationDto destination,
        String guideName,
        LocalDateTime sessionDate,
        int durationMinutes,
        int participants,
        BigDecimal totalPrice,
        String currency,
        ReviewSummaryDto review,
        boolean canReview
) {
}

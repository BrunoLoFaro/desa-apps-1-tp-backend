package com.example.desabackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingSummaryItemDto(
        Long id,
        String activityName,
        String status,
        LocalDateTime sessionStartTime,
        BigDecimal totalPrice,
        String currency
) {
}

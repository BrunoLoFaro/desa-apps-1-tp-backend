package com.example.desabackend.dto;

import com.example.desabackend.entity.ActivityCategory;
import java.math.BigDecimal;

/**
 * Compact activity card for the Home catalog list.
 */
public record ActivitySummaryDto(
        Long id,
        String name,
        DestinationDto destination,
        ActivityCategory category,
        int durationMinutes,
        BigDecimal price,
        String currency,
        int availableSpots,
        Double avgRating,
        long reviewCount
) {
}

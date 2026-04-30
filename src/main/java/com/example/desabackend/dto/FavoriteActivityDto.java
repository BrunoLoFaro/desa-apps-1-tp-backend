package com.example.desabackend.dto;

import com.example.desabackend.entity.ActivityCategory;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FavoriteActivityDto(
        Long id,
        String name,
        String imageUrl,
        DestinationDto destination,
        ActivityCategory category,
        int durationMinutes,
        BigDecimal price,
        String currency,
        int availableSpots,
        Double avgRating,
        long reviewCount,
        boolean isFavorite,
        boolean hasPriceChange,
        boolean hasAvailabilityChange,
        LocalDate startDate,
        String startTime
) {
}

package com.example.desabackend.dto;

import com.example.desabackend.entity.ActivityCategory;
import java.math.BigDecimal;
import java.util.List;

/**
 * Full activity detail for the activity screen, including upcoming sessions and computed availability.
 */
public record ActivityDetailDto(
        Long id,
        String name,
        String imageUrl,
        DestinationDto destination,
        ActivityCategory category,
        String description,
        String includesText,
        String meetingPoint,
        GuideDto guide,
        int durationMinutes,
        String language,
        String cancellationPolicy,
        BigDecimal basePrice,
        String currency,
        List<ActivitySessionDto> sessions,
        int availableSpots,
        Double avgRating,
        long reviewCount,
        boolean isFavorite,
        Integer discountPercentage
) {
}

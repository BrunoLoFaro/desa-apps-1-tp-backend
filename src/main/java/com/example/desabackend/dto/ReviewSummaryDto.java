package com.example.desabackend.dto;

import java.time.LocalDateTime;

public record ReviewSummaryDto(
        Long id,
        Integer activityRating,
        Integer guideRating,
        String comment,
        LocalDateTime createdAt
) {
}

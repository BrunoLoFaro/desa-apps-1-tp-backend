package com.example.desabackend.dto;

import java.time.LocalDateTime;

public record MyReviewDto(
        Long id,
        Long bookingId,
        Long activityId,
        String activityName,
        Integer activityRating,
        Integer guideRating,
        String comment,
        LocalDateTime createdAt
) {}

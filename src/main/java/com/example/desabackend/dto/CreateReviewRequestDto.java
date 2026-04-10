package com.example.desabackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequestDto(
        @NotNull Long bookingId,
        @NotNull @Min(1) @Max(5) Integer activityRating,
        @Min(1) @Max(5) Integer guideRating,
        @Size(max = 300) String comment
) {
}

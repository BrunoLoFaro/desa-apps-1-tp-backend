package com.example.desabackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequestDto(
        @NotNull Long sessionId,
        @NotNull @Min(1) Integer participants
) {
}

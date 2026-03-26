package com.example.desabackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A single scheduled session ("salida") for an activity, including capacity and effective price.
 */
public record ActivitySessionDto(
        Long id,
        LocalDateTime startTime,
        int capacity,
        int bookedCount,
        int availableSpots,
        BigDecimal price
) {
}

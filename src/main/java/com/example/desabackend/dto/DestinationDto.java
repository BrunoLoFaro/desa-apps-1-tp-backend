package com.example.desabackend.dto;

/**
 * Minimal destination representation for lists and activity summaries.
 */
public record DestinationDto(
        Long id,
        String name
) {
}

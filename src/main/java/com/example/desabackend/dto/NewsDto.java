package com.example.desabackend.dto;

import java.time.LocalDateTime;

/**
 * Compact news card for the mobile Home screen.
 * Represents news, offers, and featured destinations from XploreNow.
 */
public record NewsDto(
        Long id,
        String title,
        String description,
        String imageUrl,
        NewsType type,
        Long relatedActivityId,
        LocalDateTime publishedAt,
        LocalDateTime validUntil
) {
}

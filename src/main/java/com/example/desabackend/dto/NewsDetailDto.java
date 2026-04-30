package com.example.desabackend.dto;

import java.time.LocalDateTime;

/**
 * Full detail view for a news item.
 * Includes complete description and additional metadata.
 */
public record NewsDetailDto(
        Long id,
        String title,
        String description,
        String fullContent,
        String imageUrl,
        NewsType type,
        Long relatedActivityId,
        String relatedActivityName,
        LocalDateTime publishedAt,
        LocalDateTime validUntil,
        String ctaText,
        String ctaLink
) {
}

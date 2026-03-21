package com.example.desabackend.dto;

import java.util.List;

/**
 * Stable pagination wrapper for Android clients (avoid Spring's default Page JSON shape).
 */
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}

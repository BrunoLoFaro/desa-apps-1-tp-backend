package com.example.desabackend.dto;

import java.util.List;

public record UserPreferencesDto(
        List<String> preferredCategories,
        List<DestinationDto> preferredDestinations
) {
}

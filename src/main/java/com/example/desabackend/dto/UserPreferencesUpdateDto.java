package com.example.desabackend.dto;

import java.util.List;

public record UserPreferencesUpdateDto(
        List<String> preferredCategories
) {
}

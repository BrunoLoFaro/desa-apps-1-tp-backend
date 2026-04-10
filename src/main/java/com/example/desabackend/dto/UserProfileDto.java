package com.example.desabackend.dto;

import java.util.List;

public record UserProfileDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String dni,
        String phone,
        String profilePhotoUrl,
        List<String> preferredCategories,
        List<DestinationDto> preferredDestinations,
        long confirmedBookings,
        long completedBookings,
        long cancelledBookings
) {
}

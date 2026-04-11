package com.example.desabackend.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record UserProfileUpdateDto(
        @Size(min = 2, max = 80) String firstName,
        @Size(min = 2, max = 80) String lastName,
        @Size(max = 30) String phone,
        @Size(max = 500) String profilePhotoUrl,
        List<String> preferredCategories,
        List<Long> preferredDestinationIds
) {
}

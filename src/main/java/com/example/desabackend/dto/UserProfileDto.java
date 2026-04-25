package com.example.desabackend.dto;

import java.util.List;

public class UserProfileDto {
        public Long id;
        public String email;
        public String firstName;
        public String lastName;
        public String phone;
        public String profilePhotoUrl;
        public String profilePhotoBase64;
        public List<String> preferredCategories;
        public List<DestinationDto> preferredDestinations;
        public long confirmedBookings;
        public long completedBookings;
        public long cancelledBookings;

        public UserProfileDto() {}
}

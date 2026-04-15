package com.example.desabackend.controller;

import com.example.desabackend.dto.UserProfileDto;
import com.example.desabackend.dto.UserProfileUpdateDto;
import com.example.desabackend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}/profile")
    public UserProfileDto getProfile(@PathVariable Long userId) {
        return profileService.getProfile(userId);
    }

    @PutMapping(path = "/{userId}/profile", consumes = {"multipart/form-data"})
    public UserProfileDto updateProfile(
            @PathVariable Long userId,
            @RequestPart("data") @Valid UserProfileUpdateDto dto,
            @RequestPart(value = "profilePhoto", required = false) org.springframework.web.multipart.MultipartFile profilePhoto
    ) throws java.io.IOException {
        return profileService.updateProfileWithPhoto(userId, dto, profilePhoto);
    }
}

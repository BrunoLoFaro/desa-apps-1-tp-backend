package com.example.desabackend.controller;

import com.example.desabackend.dto.BookingSummaryItemDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.dto.UserPreferencesDto;
import com.example.desabackend.dto.UserPreferencesUpdateDto;
import com.example.desabackend.dto.UserProfileDto;
import com.example.desabackend.dto.UserProfileUpdateDto;
import com.example.desabackend.security.AuthUtils;
import com.example.desabackend.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}/profile")
    public UserProfileDto getProfile(@PathVariable Long userId) {
        Long authenticatedUserId = AuthUtils.getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Cannot access another user's profile");
        }
        return profileService.getProfile(userId);
    }

    @PutMapping(path = "/{userId}/profile", consumes = {"multipart/form-data"})
    public UserProfileDto updateProfile(
            @PathVariable Long userId,
            @RequestPart("data") @Valid UserProfileUpdateDto dto,
            @RequestPart(value = "profilePhoto", required = false) org.springframework.web.multipart.MultipartFile profilePhoto
    ) throws java.io.IOException {
        Long authenticatedUserId = AuthUtils.getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("Cannot modify another user's profile");
        }
        return profileService.updateProfileWithPhoto(userId, dto, profilePhoto);
    }

    // ── Preferences (sin {userId} en path — userId viene del JWT) ───────────────

    @GetMapping("/preferences")
    public UserPreferencesDto getPreferences() {
        return profileService.getPreferences(AuthUtils.getCurrentUserId());
    }

    @PutMapping("/preferences")
    public UserPreferencesDto updatePreferences(@RequestBody UserPreferencesUpdateDto dto) {
        return profileService.updatePreferences(AuthUtils.getCurrentUserId(), dto);
    }

    // ── Activity summary ─────────────────────────────────────────────────────────

    @GetMapping("/activities/summary")
    public PageResponse<BookingSummaryItemDto> getActivitySummary(
            @RequestParam(defaultValue = "0")  @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return profileService.getActivitySummary(AuthUtils.getCurrentUserId(), page, size);
    }
}

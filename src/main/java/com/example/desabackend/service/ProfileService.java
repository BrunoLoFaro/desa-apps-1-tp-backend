package com.example.desabackend.service;

import com.example.desabackend.dto.BookingSummaryItemDto;
import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.dto.UserPreferencesDto;
import com.example.desabackend.dto.UserPreferencesUpdateDto;
import com.example.desabackend.dto.UserProfileDto;
import com.example.desabackend.dto.UserProfileUpdateDto;
import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.BookingStatus;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.entity.UserPreferredCategoryEntity;
import com.example.desabackend.entity.UserPreferredDestinationEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.BookingRepository;
import com.example.desabackend.repository.DestinationRepository;
import com.example.desabackend.repository.UserPreferredCategoryRepository;
import com.example.desabackend.repository.UserPreferredDestinationRepository;
import com.example.desabackend.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserPreferredCategoryRepository categoryRepo;
    private final UserPreferredDestinationRepository destinationPrefRepo;
    private final DestinationRepository destinationRepo;
    private final BookingRepository bookingRepository;

    public ProfileService(
            UserRepository userRepository,
            UserPreferredCategoryRepository categoryRepo,
            UserPreferredDestinationRepository destinationPrefRepo,
            DestinationRepository destinationRepo,
            BookingRepository bookingRepository
    ) {
        this.userRepository = userRepository;
        this.categoryRepo = categoryRepo;
        this.destinationPrefRepo = destinationPrefRepo;
        this.destinationRepo = destinationRepo;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<String> categories = categoryRepo.findByUserId(userId).stream()
                .map(c -> c.getCategory().name())
                .toList();

        List<DestinationDto> destinations = destinationPrefRepo.findByUserId(userId).stream()
                .map(p -> destinationRepo.findById(p.getDestinationId()).orElse(null))
                .filter(d -> d != null)
                .map(d -> new DestinationDto(d.getId(), d.getName()))
                .toList();

        long confirmed = bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CONFIRMED);
        long completed = bookingRepository.countByUserIdAndStatus(userId, BookingStatus.COMPLETED);
        long cancelled = bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CANCELLED);

        return new UserProfileDto(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getDni(), user.getPhone(), user.getProfilePhotoUrl(),
                categories, destinations, confirmed, completed, cancelled
        );
    }

    // ── Preferences ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserPreferencesDto getPreferences(Long userId) {
        List<String> categories = categoryRepo.findByUserId(userId).stream()
                .map(c -> c.getCategory().name())
                .toList();
        List<DestinationDto> destinations = destinationPrefRepo.findByUserId(userId).stream()
                .map(p -> destinationRepo.findById(p.getDestinationId()).orElse(null))
                .filter(d -> d != null)
                .map(d -> new DestinationDto(d.getId(), d.getName()))
                .toList();
        return new UserPreferencesDto(categories, destinations);
    }

    @Transactional
    public UserPreferencesDto updatePreferences(Long userId, UserPreferencesUpdateDto dto) {
        if (dto.preferredCategories() != null) {
            categoryRepo.deleteByUserId(userId);
            for (String catName : dto.preferredCategories()) {
                try {
                    ActivityCategory cat = ActivityCategory.valueOf(catName);
                    UserPreferredCategoryEntity entity = new UserPreferredCategoryEntity();
                    entity.setUserId(userId);
                    entity.setCategory(cat);
                    categoryRepo.save(entity);
                } catch (IllegalArgumentException ignored) { }
            }
        }
        return getPreferences(userId);
    }

    // ── Activity Summary ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<BookingSummaryItemDto> getActivitySummary(Long userId, int page, int size) {
        Page<BookingEntity> bookingsPage = bookingRepository.findByUserId(
                userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<BookingSummaryItemDto> items = bookingsPage.getContent().stream()
                .map(b -> new BookingSummaryItemDto(
                        b.getId(),
                        b.getSession().getActivity().getName(),
                        b.getStatus().name(),
                        b.getSession().getStartTime(),
                        b.getTotalPrice(),
                        b.getSession().getActivity().getCurrency()
                ))
                .toList();
        return new PageResponse<>(items, page, size,
                bookingsPage.getTotalElements(), bookingsPage.getTotalPages());
    }

    @Transactional
    public UserProfileDto updateProfileWithPhoto(Long userId, UserProfileUpdateDto dto,
            org.springframework.web.multipart.MultipartFile profilePhoto) throws java.io.IOException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.firstName() != null) user.setFirstName(dto.firstName().trim());
        if (dto.lastName() != null) user.setLastName(dto.lastName().trim());
        if (dto.phone() != null) user.setPhone(dto.phone().trim());

        // Guardar imagen si viene como archivo; si no, usar URL provista en el DTO
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + ".jpg";
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads/profile");
            java.nio.file.Files.createDirectories(uploadDir);
            java.nio.file.Path filePath = uploadDir.resolve(fileName);
            profilePhoto.transferTo(filePath);
            user.setProfilePhotoUrl("/uploads/profile/" + fileName);
        } else if (dto.profilePhotoUrl() != null) {
            user.setProfilePhotoUrl(dto.profilePhotoUrl().trim());
        }
        userRepository.save(user);

        if (dto.preferredCategories() != null) {
            categoryRepo.deleteByUserId(userId);
            for (String catName : dto.preferredCategories()) {
                try {
                    ActivityCategory cat = ActivityCategory.valueOf(catName);
                    UserPreferredCategoryEntity entity = new UserPreferredCategoryEntity();
                    entity.setUserId(userId);
                    entity.setCategory(cat);
                    categoryRepo.save(entity);
                } catch (IllegalArgumentException ignored) { }
            }
        }

        if (dto.preferredDestinationIds() != null) {
            destinationPrefRepo.deleteByUserId(userId);
            for (Long destId : dto.preferredDestinationIds()) {
                if (destinationRepo.existsById(destId)) {
                    UserPreferredDestinationEntity entity = new UserPreferredDestinationEntity();
                    entity.setUserId(userId);
                    entity.setDestinationId(destId);
                    destinationPrefRepo.save(entity);
                }
            }
        }

        return getProfile(userId);
    }
}

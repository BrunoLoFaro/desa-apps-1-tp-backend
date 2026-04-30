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
import com.example.desabackend.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final UserPreferredCategoryRepository categoryRepo;
    private final UserPreferredDestinationRepository destinationPrefRepo;
    private final DestinationRepository destinationRepo;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public ProfileService(
            UserRepository userRepository,
            UserPreferredCategoryRepository categoryRepo,
            UserPreferredDestinationRepository destinationPrefRepo,
            DestinationRepository destinationRepo,
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository
    ) {
        this.userRepository = userRepository;
        this.categoryRepo = categoryRepo;
        this.destinationPrefRepo = destinationPrefRepo;
        this.destinationRepo = destinationRepo;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
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

        UserProfileDto dto = new UserProfileDto();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.firstName = user.getFirstName();
        dto.lastName = user.getLastName();
        dto.phone = user.getPhone();
        dto.profilePhotoUrl = null;       // imagen gestionada íntegramente en Android
        dto.profilePhotoBase64 = null;
        dto.preferredCategories = categories;
        dto.preferredDestinations = destinations;
        dto.confirmedBookings = confirmed;
        dto.completedBookings = completed;
        dto.cancelledBookings = cancelled;
        return dto;
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
            // findHistory filtra por COMPLETED y hace join fetch de destination + guide
            Page<BookingEntity> bookingsPage = bookingRepository.findHistory(
                userId, null, null, null, PageRequest.of(page, size));
            List<BookingSummaryItemDto> items = bookingsPage.getContent().stream()
                .map(b -> {
                    var a = b.getSession().getActivity();
                    boolean canReview = false;
                    if (b.getStatus() == BookingStatus.COMPLETED && !reviewRepository.existsByBookingId(b.getId())) {
                        LocalDateTime sessionEnd = b.getSession().getStartTime()
                                .plusMinutes(a.getDurationMinutes() != null ? a.getDurationMinutes() : 0);
                        canReview = !LocalDateTime.now().isAfter(sessionEnd.plusHours(48));
                    }
                    return new BookingSummaryItemDto(
                        b.getId(),
                        a.getId(),
                        a.getName(),
                        b.getStatus().name(),
                        b.getSession().getStartTime(),
                        b.getTotalPrice(),
                        a.getCurrency(),
                        a.getDestination().getName(),
                        a.getGuide() != null ? a.getGuide().getFullName() : null,
                        a.getDurationMinutes(),
                        a.getImageUrl(),
                        canReview
                    );
                })
                .toList();
            return new PageResponse<>(items, page, size,
                bookingsPage.getTotalElements(), bookingsPage.getTotalPages());
            }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UserProfileUpdateDto dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.firstName() != null) user.setFirstName(dto.firstName().trim());
        if (dto.lastName() != null) user.setLastName(dto.lastName().trim());
        if (dto.phone() != null) user.setPhone(dto.phone().trim());
        // Imagen gestionada íntegramente en Android — no se guarda en backend
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

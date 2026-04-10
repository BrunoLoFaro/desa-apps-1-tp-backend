package com.example.desabackend.service;

import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.UserProfileDto;
import com.example.desabackend.dto.UserProfileUpdateDto;
import com.example.desabackend.entity.ActivityCategory;
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

    @Transactional
    public UserProfileDto updateProfile(Long userId, UserProfileUpdateDto dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.firstName() != null) user.setFirstName(dto.firstName().trim());
        if (dto.lastName() != null) user.setLastName(dto.lastName().trim());
        if (dto.phone() != null) user.setPhone(dto.phone().trim());
        if (dto.profilePhotoUrl() != null) user.setProfilePhotoUrl(dto.profilePhotoUrl().trim());
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

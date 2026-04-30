package com.example.desabackend.service;

import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.FavoriteActivityDto;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.Favorite;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.FavoriteRepository;
import com.example.desabackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivityCatalogService activityCatalogService;
    private final ActivitySessionRepository sessionRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           ActivityRepository activityRepository,
                           ActivityCatalogService activityCatalogService,
                           ActivitySessionRepository sessionRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.activityCatalogService = activityCatalogService;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void addFavorite(Long userId, Long activityId) {
        if (favoriteRepository.existsByUserIdAndActivityId(userId, activityId)) {
            return;
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        List<ActivitySummaryDto> summaries = activityCatalogService.getActivitiesByIds(List.of(activityId), userId);
        BigDecimal snapshotPrice = summaries.isEmpty() ? activity.getBasePrice() : summaries.get(0).price();
        int snapshotSlots = summaries.isEmpty() ? 0 : summaries.get(0).availableSpots();

        favoriteRepository.save(new Favorite(user, activity, snapshotPrice, snapshotSlots));
    }

    @Transactional
    public void removeFavorite(Long userId, Long activityId) {
        Favorite favorite = favoriteRepository.findByUserIdAndActivityId(userId, activityId)
                .orElseThrow(() -> new NotFoundException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteActivityDto> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(userId);
        if (favorites.isEmpty()) return List.of();

        List<Long> activityIds = favorites.stream()
                .map(f -> f.getActivity().getId())
                .toList();

        List<ActivitySummaryDto> currentSummaries = activityCatalogService.getActivitiesByIds(activityIds, userId);
        Map<Long, ActivitySummaryDto> currentById = currentSummaries.stream()
                .collect(Collectors.toMap(ActivitySummaryDto::id, s -> s, (a, b) -> a));

        Map<Long, LocalDateTime> nextDateTimeById = sessionRepository
                .findNextSessionDateByActivityIds(activityIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(
                        ActivitySessionRepository.NextSessionDateProjection::getActivityId,
                        ActivitySessionRepository.NextSessionDateProjection::getNextSessionDate,
                        (a, b) -> a
                ));

        Map<Long, Favorite> favoriteByActivityId = favorites.stream()
                .collect(Collectors.toMap(f -> f.getActivity().getId(), f -> f, (a, b) -> a));

        return activityIds.stream()
                .map(id -> {
                    ActivitySummaryDto current = currentById.get(id);
                    Favorite fav = favoriteByActivityId.get(id);
                    if (current == null) return null;

                    boolean hasPriceChange = fav.getSnapshotPrice() != null
                            && current.price() != null
                            && current.price().compareTo(fav.getSnapshotPrice()) != 0;

                    boolean hasAvailabilityChange = fav.getSnapshotSlots() != null
                            && current.availableSpots() > fav.getSnapshotSlots();

                    LocalDateTime nextDt = nextDateTimeById.get(id);
                    LocalDate startDate = nextDt != null ? nextDt.toLocalDate() : null;
                    String startTime = nextDt != null
                            ? String.format("%02d:%02d", nextDt.getHour(), nextDt.getMinute())
                            : null;

                    return new FavoriteActivityDto(
                            id,
                            current.name(),
                            current.imageUrl(),
                            current.destination(),
                            current.category(),
                            current.durationMinutes(),
                            current.price(),
                            current.currency(),
                            current.availableSpots(),
                            current.avgRating(),
                            current.reviewCount(),
                            true,
                            hasPriceChange,
                            hasAvailabilityChange,
                            startDate,
                            startTime
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

}

package com.example.desabackend.service;

import com.example.desabackend.dto.ActivityDetailDto;
import com.example.desabackend.dto.ActivitySessionDto;
import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * Catalog read model for the Home screen:
 * - paginated list with combined filters
 * - detail view with sessions
 *
 * Uses aggregated queries to avoid N+1 when computing price/availability for cards.
 */
public class ActivityCatalogService {

    private final ActivityRepository activityRepository;
    private final ActivitySessionRepository sessionRepository;
    private final ReviewRepository reviewRepository;

    public ActivityCatalogService(ActivityRepository activityRepository, ActivitySessionRepository sessionRepository, ReviewRepository reviewRepository) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivitySummaryDto> listActivities(
            Integer page,
            Integer size,
            Long destinationId,
            ActivityCategory category,
            LocalDate date,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean featuredOnly
    ) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);

        LocalDateTime now = LocalDateTime.now();
        var spec = ActivitySpecifications.forCatalog(destinationId, category, date, minPrice, maxPrice, now, featuredOnly);
        Page<ActivityEntity> activitiesPage = activityRepository.findAll(spec, pageable);

        List<Long> activityIds = activitiesPage.getContent().stream()
                .map(ActivityEntity::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, ActivitySessionRepository.ActivitySummaryAggregate> aggregatesByActivityId =
                aggregateForSummary(activityIds, date, minPrice, maxPrice, now).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                ActivitySessionRepository.ActivitySummaryAggregate::getActivityId,
                                Function.identity(),
                                (a, b) -> a
                        ));
        Map<Long, ReviewRepository.ActivityRatingAggregate> ratingsByActivityId =
                (activityIds.isEmpty() ? List.<ReviewRepository.ActivityRatingAggregate>of()
                        : reviewRepository.aggregateActivityRatings(activityIds))
                        .stream()
                        .collect(java.util.stream.Collectors.toMap(
                                ReviewRepository.ActivityRatingAggregate::getActivityId,
                                Function.identity(),
                                (a, b) -> a
                        ));
        List<ActivitySummaryDto> items = activitiesPage.getContent().stream()
                .map(a -> ActivityDtoMapper.toSummaryDto(a, aggregatesByActivityId.get(a.getId()), ratingsByActivityId.get(a.getId())))
                .toList();

        return new PageResponse<>(
                items,
                activitiesPage.getNumber(),
                activitiesPage.getSize(),
                activitiesPage.getTotalElements(),
                activitiesPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ActivityDetailDto getActivityDetail(Long activityId, LocalDate date) {
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found: " + activityId));

        List<ActivitySessionEntity> sessions = fetchSessions(activityId, date);
        List<ActivitySessionDto> sessionDtos = sessions.stream()
                .map(ActivityDtoMapper::toSessionDto)
                .toList();

        int availableSpots = calculateAvailableSpotsForDetail(sessions, date);

        ReviewRepository.ActivityRatingAggregate ratingAgg = reviewRepository.getActivityRating(activityId).orElse(null);
        Double avgRating = ratingAgg != null ? ratingAgg.getAvgRating() : null;
        long reviewCount = ratingAgg != null && ratingAgg.getReviewCount() != null ? ratingAgg.getReviewCount() : 0L;

        return new ActivityDetailDto(
                activity.getId(),
                activity.getName(),
                ActivityDtoMapper.toDestinationDto(activity),
                activity.getCategory(),
                activity.getDescription(),
                activity.getIncludesText(),
                activity.getMeetingPoint(),
                ActivityDtoMapper.toGuideDto(activity),
                ActivityDtoMapper.nonNullInt(activity.getDurationMinutes()),
                activity.getLanguage(),
                activity.getCancellationPolicy(),
                activity.getBasePrice(),
                activity.getCurrency(),
                sessionDtos,
                availableSpots,
                avgRating,
                reviewCount
        );
    }

    private List<ActivitySessionRepository.ActivitySummaryAggregate> aggregateForSummary(List<Long> activityIds, LocalDate date) {
        return aggregateForSummary(activityIds, date, null, null, LocalDateTime.now());
    }

    private List<ActivitySessionRepository.ActivitySummaryAggregate> aggregateForSummary(
            List<Long> activityIds,
            LocalDate date,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDateTime now
    ) {
        if (activityIds.isEmpty()) {
            return List.of();
        }

        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            return sessionRepository.aggregateForDate(activityIds, start, end, minPrice, maxPrice);
        }

        return sessionRepository.aggregateForNextSession(activityIds, now == null ? LocalDateTime.now() : now);
    }

    private List<ActivitySessionEntity> fetchSessions(Long activityId, LocalDate date) {
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            return sessionRepository.findByActivityIdAndDay(activityId, start, end);
        }
        return sessionRepository.findFutureByActivityId(activityId, LocalDateTime.now());
    }

    private static int calculateAvailableSpotsForDetail(List<ActivitySessionEntity> sessions, LocalDate date) {
        if (sessions.isEmpty()) {
            return 0;
        }

        if (date != null) {
            return sessions.stream()
                    .mapToInt(s -> Math.max(0, ActivityDtoMapper.nonNullInt(s.getCapacity()) - ActivityDtoMapper.nonNullInt(s.getBookedCount())))
                    .sum();
        }

        LocalDateTime firstStartTime = sessions.get(0).getStartTime();
        return sessions.stream()
                .filter(s -> Objects.equals(firstStartTime, s.getStartTime()))
                .mapToInt(s -> Math.max(0, ActivityDtoMapper.nonNullInt(s.getCapacity()) - ActivityDtoMapper.nonNullInt(s.getBookedCount())))
                .sum();
    }
}

package com.example.desabackend.service;

import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.FavoriteRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
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
 * Recommended activities use-case.
 * Ranking is based on user preferences (destinations/categories) with simple scoring and deterministic tie-breakers.
 */
public class RecommendationService {

    private final ActivityRepository activityRepository;
    private final ActivitySessionRepository sessionRepository;
    private final ReviewRepository reviewRepository;
    private final UserPreferenceService userPreferenceService;
    private final FavoriteRepository favoriteRepository;

    public RecommendationService(
            ActivityRepository activityRepository,
            ActivitySessionRepository sessionRepository,
            ReviewRepository reviewRepository,
            UserPreferenceService userPreferenceService,
            FavoriteRepository favoriteRepository
    ) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
        this.reviewRepository = reviewRepository;
        this.userPreferenceService = userPreferenceService;
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ActivitySummaryDto> listRecommended(
            long userId,
            Integer page,
            Integer size,
            Long destinationId,
            ActivityCategory category
    ) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(safePage, safeSize);

        List<Long> preferredDestIds = userPreferenceService.getPreferredDestinationIds(userId);
        List<ActivityCategory> preferredCategories = userPreferenceService.getPreferredCategories(userId);

        boolean prefDestEmpty = preferredDestIds.isEmpty();
        boolean prefCatEmpty = preferredCategories.isEmpty();

        if (prefDestEmpty) {
            preferredDestIds = List.of(0L);
        }
        if (prefCatEmpty) {
            preferredCategories = List.of(ActivityCategory.OTRA);
        }

        Page<ActivityEntity> pageResult = activityRepository.findRecommended(
                destinationId,
                category,
                false,
                preferredDestIds,
                preferredCategories,
                prefDestEmpty,
                prefCatEmpty,
                LocalDateTime.now(),
                pageable
        );

        List<Long> activityIds = pageResult.getContent().stream()
                .map(ActivityEntity::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, ActivitySessionRepository.ActivitySummaryAggregate> aggregatesByActivityId =
                (activityIds.isEmpty() ? List.<ActivitySessionRepository.ActivitySummaryAggregate>of()
                        : sessionRepository.aggregateForNextSession(activityIds, LocalDateTime.now()))
                        .stream()
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

        var favoriteIds = new HashSet<>(favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(f -> f.getActivity().getId())
                .toList());

        List<ActivitySummaryDto> items = pageResult.getContent().stream()
                .map(a -> ActivityDtoMapper.toSummaryDto(
                        a,
                        aggregatesByActivityId.get(a.getId()),
                        ratingsByActivityId.get(a.getId()),
                        favoriteIds.contains(a.getId())
                ))
                .toList();

        return new PageResponse<>(
                items,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}

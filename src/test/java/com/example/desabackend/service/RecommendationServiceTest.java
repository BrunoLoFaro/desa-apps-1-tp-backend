package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.DestinationEntity;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ActivitySessionRepository sessionRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserPreferenceService userPreferenceService;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationService(activityRepository, sessionRepository, reviewRepository, userPreferenceService);
    }

    @Test
    void listRecommended_includesAverageRatingAndReviewCountInSummary() {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(5L);
        activity.setName("Wine Tour");
        activity.setCategory(ActivityCategory.GASTRONOMIA);
        activity.setDurationMinutes(180);
        activity.setBasePrice(new BigDecimal("35000.00"));
        activity.setCurrency("ARS");

        DestinationEntity destination = new DestinationEntity();
        destination.setId(2L);
        destination.setName("Mendoza");
        activity.setDestination(destination);
        ActivitySessionRepository.ActivitySummaryAggregate sessionAgg =
            sessionAggregate(5L, 4L, new BigDecimal("32000.00"));
        ReviewRepository.ActivityRatingAggregate ratingAgg = ratingAggregate(5L, 4.2, 6L);

        when(userPreferenceService.getPreferredDestinationIds(99L)).thenReturn(List.of(2L));
        when(userPreferenceService.getPreferredCategories(99L)).thenReturn(List.of(ActivityCategory.GASTRONOMIA));
        when(activityRepository.findRecommended(any(), any(), eq(false), any(), any(), eq(false), eq(false), any(LocalDateTime.class), any()))
                .thenReturn(new PageImpl<>(List.of(activity)));
        when(sessionRepository.aggregateForNextSession(eq(List.of(5L)), any(LocalDateTime.class)))
            .thenReturn(List.of(sessionAgg));
        when(reviewRepository.aggregateActivityRatings(eq(List.of(5L))))
            .thenReturn(List.of(ratingAgg));

        var result = service.listRecommended(99L, 0, 10, null, null);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).avgRating()).isEqualTo(4.2);
        assertThat(result.items().get(0).reviewCount()).isEqualTo(6L);
        assertThat(result.items().get(0).price()).isEqualByComparingTo("32000.00");
    }

    private static ActivitySessionRepository.ActivitySummaryAggregate sessionAggregate(
            Long activityId, Long availableSpots, BigDecimal price) {
        ActivitySessionRepository.ActivitySummaryAggregate aggregate =
                mock(ActivitySessionRepository.ActivitySummaryAggregate.class);
        when(aggregate.getActivityId()).thenReturn(activityId);
        when(aggregate.getAvailableSpots()).thenReturn(availableSpots);
        when(aggregate.getPrice()).thenReturn(price);
        return aggregate;
    }

    private static ReviewRepository.ActivityRatingAggregate ratingAggregate(Long activityId, Double avgRating, Long reviewCount) {
        ReviewRepository.ActivityRatingAggregate aggregate = mock(ReviewRepository.ActivityRatingAggregate.class);
        when(aggregate.getActivityId()).thenReturn(activityId);
        when(aggregate.getAvgRating()).thenReturn(avgRating);
        when(aggregate.getReviewCount()).thenReturn(reviewCount);
        return aggregate;
    }
}
package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivityItineraryPointEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.entity.DestinationEntity;
import com.example.desabackend.entity.GuideEntity;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.FavoriteRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityCatalogServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ActivitySessionRepository sessionRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private FavoriteRepository favoriteRepository;

    private ActivityCatalogService service;

    @BeforeEach
    void setUp() {
        service = new ActivityCatalogService(activityRepository, sessionRepository, reviewRepository, favoriteRepository);
    }

    @Test
    void listActivities_includesAverageRatingAndReviewCountInSummary() {
        ActivityEntity activity = createActivity(1L, "Rafting");
        ActivitySessionRepository.ActivitySummaryAggregate sessionAgg =
            sessionAggregate(1L, 5L, new BigDecimal("9500.00"));
        ReviewRepository.ActivityRatingAggregate ratingAgg = ratingAggregate(1L, 4.8, 23L);

        when(activityRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(activity)));
        when(sessionRepository.aggregateForNextSession(eq(List.of(1L)), any(LocalDateTime.class)))
            .thenReturn(List.of(sessionAgg));
        when(reviewRepository.aggregateActivityRatings(eq(List.of(1L))))
            .thenReturn(List.of(ratingAgg));
        when(favoriteRepository.findByUserIdOrderByCreatedAtDesc(77L)).thenReturn(List.of());

        var result = service.listActivities(0, 10, null, null, null, null, null, false, 77L);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).avgRating()).isEqualTo(4.8);
        assertThat(result.items().get(0).reviewCount()).isEqualTo(23L);
        assertThat(result.items().get(0).price()).isEqualByComparingTo("9500.00");
    }

    @Test
    void getActivityDetail_withoutReviews_defaultsToNullRatingAndZeroCount() {
        ActivityEntity activity = createActivity(2L, "City Tour");
        ActivitySessionEntity session = new ActivitySessionEntity();
        session.setId(100L);
        session.setActivity(activity);
        session.setStartTime(LocalDateTime.now().plusDays(1));
        session.setCapacity(10);
        session.setBookedCount(3);
        session.setPriceOverride(new BigDecimal("5000.00"));

        when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        when(sessionRepository.findFutureByActivityId(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(session));
        when(reviewRepository.getActivityRating(2L)).thenReturn(Optional.empty());
        when(favoriteRepository.existsByUserIdAndActivityId(77L, 2L)).thenReturn(false);

        var result = service.getActivityDetail(2L, null, 77L);

        assertThat(result.avgRating()).isNull();
        assertThat(result.reviewCount()).isZero();
        assertThat(result.availableSpots()).isEqualTo(7);
        assertThat(result.sessions()).hasSize(1);
    }

    @Test
    void getActivityDetail_includesItineraryPointsSortedByPosition() {
        ActivityEntity activity = createActivity(3L, "Recorrido");
        activity.setMeetingPoint("Plaza Dorrego");

        List<ActivityItineraryPointEntity> points = new ArrayList<>();
        points.add(itineraryPoint(activity, 2, "Mercado de San Telmo"));
        points.add(itineraryPoint(activity, 1, "Plaza Dorrego"));
        activity.setItineraryPoints(points);

        when(activityRepository.findById(3L)).thenReturn(Optional.of(activity));
        when(sessionRepository.findFutureByActivityId(eq(3L), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(reviewRepository.getActivityRating(3L)).thenReturn(Optional.empty());
        when(favoriteRepository.existsByUserIdAndActivityId(77L, 3L)).thenReturn(false);

        var result = service.getActivityDetail(3L, null, 77L);

        assertThat(result.itineraryPoints()).hasSize(2);
        assertThat(result.itineraryPoints().get(0).position()).isEqualTo(1);
        assertThat(result.itineraryPoints().get(0).name()).isEqualTo("Plaza Dorrego");
        assertThat(result.itineraryPoints().get(1).position()).isEqualTo(2);
        assertThat(result.itineraryPoints().get(1).name()).isEqualTo("Mercado de San Telmo");
    }

    private static ActivityEntity createActivity(Long id, String name) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(id);
        activity.setName(name);
        activity.setCategory(ActivityCategory.AVENTURA);
        activity.setDescription("desc");
        activity.setIncludesText("includes");
        activity.setMeetingPoint("meeting");
        activity.setDurationMinutes(120);
        activity.setLanguage("es");
        activity.setCancellationPolicy("flex");
        activity.setBasePrice(new BigDecimal("10000.00"));
        activity.setCurrency("ARS");

        DestinationEntity destination = new DestinationEntity();
        destination.setId(10L);
        destination.setName("Mendoza");
        activity.setDestination(destination);

        GuideEntity guide = new GuideEntity();
        guide.setId(20L);
        guide.setFullName("Ana Guide");
        activity.setGuide(guide);
        return activity;
    }

    private static ActivityItineraryPointEntity itineraryPoint(ActivityEntity activity, int position, String name) {
        ActivityItineraryPointEntity point = new ActivityItineraryPointEntity();
        point.setActivity(activity);
        point.setPosition(position);
        point.setName(name);
        point.setAddress(name);
        return point;
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

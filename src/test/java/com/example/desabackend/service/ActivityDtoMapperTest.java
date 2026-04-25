package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.DestinationEntity;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActivityDtoMapperTest {

    @Test
    void toSummaryDto_mapsRatingFieldsAndFallbacks() {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(7L);
        activity.setName("Kayak");
        activity.setCategory(ActivityCategory.AVENTURA);
        activity.setDurationMinutes(90);
        activity.setBasePrice(new BigDecimal("1500.00"));
        activity.setCurrency("ARS");

        DestinationEntity destination = new DestinationEntity();
        destination.setId(3L);
        destination.setName("Ushuaia");
        activity.setDestination(destination);

        ActivitySessionRepository.ActivitySummaryAggregate sessionAgg =
                mock(ActivitySessionRepository.ActivitySummaryAggregate.class);
        when(sessionAgg.getPrice()).thenReturn(new BigDecimal("1200.00"));
        when(sessionAgg.getAvailableSpots()).thenReturn(8L);

        ReviewRepository.ActivityRatingAggregate ratingAgg =
                mock(ReviewRepository.ActivityRatingAggregate.class);
        when(ratingAgg.getAvgRating()).thenReturn(4.6);
        when(ratingAgg.getReviewCount()).thenReturn(11L);

        var dto = ActivityDtoMapper.toSummaryDto(activity, sessionAgg, ratingAgg, true);

        assertThat(dto.price()).isEqualByComparingTo("1200.00");
        assertThat(dto.availableSpots()).isEqualTo(8);
        assertThat(dto.avgRating()).isEqualTo(4.6);
        assertThat(dto.reviewCount()).isEqualTo(11L);
        assertThat(dto.destination().name()).isEqualTo("Ushuaia");
    }

    @Test
    void toSummaryDto_withoutRatingAggregate_defaultsToNullAndZero() {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(8L);
        activity.setName("Walking Tour");
        activity.setCategory(ActivityCategory.FREE_TOUR);
        activity.setDurationMinutes(60);
        activity.setBasePrice(new BigDecimal("0.00"));
        activity.setCurrency("ARS");

        DestinationEntity destination = new DestinationEntity();
        destination.setId(4L);
        destination.setName("CABA");
        activity.setDestination(destination);

        var dto = ActivityDtoMapper.toSummaryDto(activity, null, null, false);

        assertThat(dto.price()).isEqualByComparingTo("0.00");
        assertThat(dto.availableSpots()).isZero();
        assertThat(dto.avgRating()).isNull();
        assertThat(dto.reviewCount()).isZero();
    }
}
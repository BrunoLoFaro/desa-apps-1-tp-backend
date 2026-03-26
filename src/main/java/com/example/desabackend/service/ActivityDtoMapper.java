package com.example.desabackend.service;

import com.example.desabackend.dto.ActivitySessionDto;
import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.GuideDto;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.repository.ActivitySessionRepository;
import java.math.BigDecimal;

final class ActivityDtoMapper {

    /**
     * Centralized DTO mapping to keep controller/service code small and consistent.
     */
    private ActivityDtoMapper() {
    }

    static ActivitySummaryDto toSummaryDto(ActivityEntity activity, ActivitySessionRepository.ActivitySummaryAggregate agg) {
        BigDecimal price = agg != null && agg.getPrice() != null ? agg.getPrice() : activity.getBasePrice();
        int availableSpots = agg != null && agg.getAvailableSpots() != null ? Math.toIntExact(agg.getAvailableSpots()) : 0;

        return new ActivitySummaryDto(
                activity.getId(),
                activity.getName(),
                toDestinationDto(activity),
                activity.getCategory(),
                nonNullInt(activity.getDurationMinutes()),
                price,
                activity.getCurrency(),
                availableSpots
        );
    }

    static ActivitySessionDto toSessionDto(ActivitySessionEntity session) {
        int capacity = nonNullInt(session.getCapacity());
        int booked = nonNullInt(session.getBookedCount());
        int available = Math.max(0, capacity - booked);

        BigDecimal price = session.getPriceOverride();
        if (price == null && session.getActivity() != null) {
            price = session.getActivity().getBasePrice();
        }

        return new ActivitySessionDto(
                session.getId(),
                session.getStartTime(),
                capacity,
                booked,
                available,
                price
        );
    }

    static DestinationDto toDestinationDto(ActivityEntity activity) {
        if (activity.getDestination() == null) {
            return null;
        }
        return new DestinationDto(activity.getDestination().getId(), activity.getDestination().getName());
    }

    static GuideDto toGuideDto(ActivityEntity activity) {
        if (activity.getGuide() == null) {
            return null;
        }
        return new GuideDto(activity.getGuide().getId(), activity.getGuide().getFullName());
    }

    static int nonNullInt(Integer value) {
        return value == null ? 0 : value;
    }
}

package com.example.desabackend.service;

import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.HistoryItemDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.dto.ReviewSummaryDto;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.ReviewEntity;
import com.example.desabackend.repository.BookingRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoryService {


    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public HistoryService(BookingRepository bookingRepository, ReviewRepository reviewRepository) {
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<HistoryItemDto> getHistory(Long userId, Long destinationId,
            LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));
        LocalDateTime startDt = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDt = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        Page<BookingEntity> result = bookingRepository.findHistory(userId, destinationId, startDt, endDt,
                PageRequest.of(safePage, safeSize));
        List<HistoryItemDto> items = result.getContent().stream().map(this::toHistoryItem).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

        private HistoryItemDto toHistoryItem(BookingEntity booking) {
        ActivitySessionEntity session = booking.getSession();
        ActivityEntity activity = session.getActivity();
        DestinationDto dest = activity.getDestination() != null
            ? new DestinationDto(activity.getDestination().getId(), activity.getDestination().getName()) : null;
        String guideName = activity.getGuide() != null ? activity.getGuide().getFullName() : null;
        ReviewSummaryDto reviewDto = reviewRepository.findByBookingId(booking.getId())
            .map(r -> new ReviewSummaryDto(r.getId(), r.getActivityRating(), r.getGuideRating(), r.getComment(), r.getCreatedAt()))
            .orElse(null);
        // canReview ya no se calcula aquí
        return new HistoryItemDto(booking.getId(), activity.getId(), activity.getName(), dest, guideName,
            session.getStartTime(), activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 0,
            booking.getParticipants(), booking.getTotalPrice(), activity.getCurrency(), reviewDto, false);
        }
}

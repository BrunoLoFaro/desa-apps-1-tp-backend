package com.example.desabackend.service;

import com.example.desabackend.dto.CreateReviewRequestDto;
import com.example.desabackend.dto.MyReviewDto;
import com.example.desabackend.dto.ReviewSummaryDto;
import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.BookingStatus;
import com.example.desabackend.entity.ReviewEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.BookingRepository;
import com.example.desabackend.repository.ReviewRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private static final int REVIEW_WINDOW_HOURS = 48;

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ReviewSummaryDto createReview(Long userId, CreateReviewRequestDto request) {
        BookingEntity booking = bookingRepository.findByIdAndUserId(request.bookingId(), userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Can only review completed bookings");
        }

        LocalDateTime sessionEnd = booking.getSession().getStartTime()
                .plusMinutes(booking.getSession().getActivity().getDurationMinutes() != null
                        ? booking.getSession().getActivity().getDurationMinutes()
                        : 0);

        if (LocalDateTime.now().isAfter(sessionEnd.plusHours(REVIEW_WINDOW_HOURS))) {
            throw new IllegalArgumentException("Review window has expired (48 hours after activity ends)");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new IllegalArgumentException("A review already exists for this booking");
        }

        ReviewEntity review = new ReviewEntity();
        review.setBooking(booking);
        review.setUser(booking.getUser());
        review.setActivity(booking.getSession().getActivity());
        review.setGuide(booking.getSession().getActivity().getGuide());
        review.setActivityRating(request.activityRating());
        review.setGuideRating(request.guideRating());
        review.setComment(request.comment() != null ? request.comment().trim() : null);

        ReviewEntity saved = reviewRepository.save(review);

        return new ReviewSummaryDto(
                saved.getId(),
                saved.getActivityRating(),
                saved.getGuideRating(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ReviewSummaryDto getReviewByBooking(Long userId, Long bookingId) {
        ReviewEntity review = reviewRepository.findByBookingIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        return new ReviewSummaryDto(
                review.getId(),
                review.getActivityRating(),
                review.getGuideRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<MyReviewDto> getMyReviews(Long userId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .map(r -> new MyReviewDto(
                        r.getId(),
                        r.getBooking().getId(),
                        r.getActivity().getId(),
                        r.getActivity().getName(),
                        r.getActivityRating(),
                        r.getGuideRating(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
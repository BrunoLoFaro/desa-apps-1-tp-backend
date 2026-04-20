package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.BookingStatus;
import com.example.desabackend.entity.DestinationEntity;
import com.example.desabackend.entity.GuideEntity;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.BookingRepository;
import com.example.desabackend.repository.ReviewRepository;
import com.example.desabackend.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceReviewWindowTest {

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ActivitySessionRepository sessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;

    // ── helpers ────────────────────────────────────────────────

    private BookingService serviceAt(LocalDateTime now) {
        Instant instant = now.atZone(ZONE).toInstant();
        Clock fixed = Clock.fixed(instant, ZONE);
        return new BookingService(bookingRepository, sessionRepository, userRepository, reviewRepository, fixed);
    }

    private BookingEntity completedBooking(LocalDateTime sessionStart, int durationMinutes) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(1L);
        activity.setName("Rafting");
        activity.setCategory(ActivityCategory.AVENTURA);
        activity.setDurationMinutes(durationMinutes);
        activity.setBasePrice(new BigDecimal("10000.00"));
        activity.setCurrency("ARS");
        activity.setCancellationPolicy("flex");

        DestinationEntity dest = new DestinationEntity();
        dest.setId(10L);
        dest.setName("Mendoza");
        activity.setDestination(dest);

        GuideEntity guide = new GuideEntity();
        guide.setId(20L);
        guide.setFullName("Ana Guide");
        activity.setGuide(guide);

        ActivitySessionEntity session = new ActivitySessionEntity();
        session.setId(100L);
        session.setActivity(activity);
        session.setStartTime(sessionStart);
        session.setCapacity(10);
        session.setBookedCount(2);

        BookingEntity booking = new BookingEntity();
        booking.setId(1L);
        booking.setSession(session);
        booking.setParticipants(2);
        booking.setTotalPrice(new BigDecimal("20000.00"));
        booking.setStatus(BookingStatus.COMPLETED);
        return booking;
    }

    // ── tests ─────────────────────────────────────────────────

    @Test
    void canReview_withinWindow_returnsTrue() {
        // Session: Apr 10 at 10:00, duration 120 min → ends 12:00
        // 48h window expires Apr 12 at 12:00
        // "Now" = Apr 11 at 10:00 → within window
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 11, 10, 0);

        BookingEntity booking = completedBooking(sessionStart, 120);
        when(reviewRepository.existsByBookingId(1L)).thenReturn(false);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isTrue();
    }

    @Test
    void canReview_exactly48hAfterSessionEnd_returnsFalse() {
        // Session: Apr 10 at 10:00, duration 120 min → ends 12:00
        // 48h window expires Apr 12 at 12:00
        // "Now" = Apr 12 at 12:00:01 → window expired
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0, 1);

        BookingEntity booking = completedBooking(sessionStart, 120);
        when(reviewRepository.existsByBookingId(1L)).thenReturn(false);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isFalse();
    }

    @Test
    void canReview_wellAfter48h_returnsFalse() {
        // Session: Apr 5 at 09:00, duration 60 min → ends 10:00
        // 48h window expires Apr 7 at 10:00
        // "Now" = Apr 15 → way past the window
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 5, 9, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 15, 12, 0);

        BookingEntity booking = completedBooking(sessionStart, 60);
        when(reviewRepository.existsByBookingId(1L)).thenReturn(false);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isFalse();
    }

    @Test
    void canReview_alreadyReviewed_returnsFalse() {
        // Within 48h window but review already exists
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 11, 10, 0);

        BookingEntity booking = completedBooking(sessionStart, 120);
        when(reviewRepository.existsByBookingId(1L)).thenReturn(true);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isFalse();
    }

    @Test
    void canReview_notCompleted_returnsFalse() {
        // Booking is CONFIRMED, not COMPLETED → canReview should be false regardless
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 11, 10, 0);

        BookingEntity booking = completedBooking(sessionStart, 120);
        booking.setStatus(BookingStatus.CONFIRMED);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isFalse();
    }

    @Test
    void canReview_atExactBoundary_returnsTrue() {
        // Session: Apr 10 at 10:00, duration 120 min → ends 12:00
        // 48h window expires Apr 12 at 12:00
        // "Now" = Apr 12 at 12:00:00 exactly → NOT after → canReview = true
        LocalDateTime sessionStart = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime now = LocalDateTime.of(2026, 4, 12, 12, 0, 0);

        BookingEntity booking = completedBooking(sessionStart, 120);
        when(reviewRepository.existsByBookingId(1L)).thenReturn(false);

        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(booking));
        when(bookingRepository.findByUserId(null, org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))))
                .thenReturn(page);

        BookingService service = serviceAt(now);
        var result = service.listMyBookings(null, null, 0, 10);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).canReview()).isTrue();
    }
}

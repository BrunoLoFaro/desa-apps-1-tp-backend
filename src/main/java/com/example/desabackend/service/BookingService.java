package com.example.desabackend.service;

import com.example.desabackend.dto.BookingDto;
import com.example.desabackend.dto.CreateBookingRequestDto;
import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.BookingStatus;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.BookingRepository;
import com.example.desabackend.repository.ReviewRepository;
import com.example.desabackend.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private static final int REVIEW_WINDOW_HOURS = 48;

    private final BookingRepository bookingRepository;
    private final ActivitySessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final Clock clock;

    public BookingService(BookingRepository bookingRepository, ActivitySessionRepository sessionRepository,
                          UserRepository userRepository, ReviewRepository reviewRepository, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.clock = clock;
    }

    @Transactional
    public BookingDto createBooking(Long userId, CreateBookingRequestDto request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ActivitySessionEntity session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new NotFoundException("Session not found"));

        int available = session.getCapacity() - session.getBookedCount();
        if (request.participants() > available) {
            throw new IllegalArgumentException("Not enough spots available (requested "
                    + request.participants() + ", available " + available + ")");
        }
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book a session that has already started");
        }

        BigDecimal unitPrice = session.getPriceOverride() != null
                ? session.getPriceOverride() : session.getActivity().getBasePrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.participants()));

        session.setBookedCount(session.getBookedCount() + request.participants());
        sessionRepository.save(session);

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setSession(session);
        booking.setParticipants(request.participants());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        return toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto cancelBooking(Long userId, Long bookingId) {
        BookingEntity booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed bookings can be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        ActivitySessionEntity session = booking.getSession();
        session.setBookedCount(Math.max(0, session.getBookedCount() - booking.getParticipants()));
        sessionRepository.save(session);
        return toDto(booking);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingDto> listMyBookings(Long userId, String statusFilter, Integer page, Integer size) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));
        PageRequest pageRequest = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<BookingEntity> result;
        if (statusFilter != null && !statusFilter.isBlank()) {
            List<BookingStatus> statuses = Arrays.stream(statusFilter.split(","))
                    .map(String::trim).map(BookingStatus::valueOf).toList();
            result = bookingRepository.findByUserIdAndStatusIn(userId, statuses, pageRequest);
        } else {
            result = bookingRepository.findByUserId(userId, pageRequest);
        }
        List<BookingDto> items = result.getContent().stream().map(this::toDto).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private BookingDto toDto(BookingEntity booking) {
        ActivitySessionEntity session = booking.getSession();
        ActivityEntity activity = session.getActivity();
        DestinationDto dest = activity.getDestination() != null
                ? new DestinationDto(activity.getDestination().getId(), activity.getDestination().getName()) : null;
        String guideName = activity.getGuide() != null ? activity.getGuide().getFullName() : null;
        boolean canReview = false;
        if (booking.getStatus() == BookingStatus.COMPLETED && !reviewRepository.existsByBookingId(booking.getId())) {
            LocalDateTime sessionEnd = session.getStartTime()
                    .plusMinutes(activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 0);
            canReview = !LocalDateTime.now(clock).isAfter(sessionEnd.plusHours(REVIEW_WINDOW_HOURS));
        }
        return new BookingDto(booking.getId(), session.getId(), activity.getId(), activity.getName(), dest, guideName,
                session.getStartTime(), activity.getDurationMinutes() != null ? activity.getDurationMinutes() : 0,
                booking.getParticipants(), booking.getTotalPrice(), activity.getCurrency(),
                booking.getStatus(), activity.getCancellationPolicy(),
                booking.getCreatedAt(), booking.getCancelledAt(), canReview, booking.getVoucherCode());
    }
}

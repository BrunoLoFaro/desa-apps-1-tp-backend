package com.example.desabackend.service;

import com.example.desabackend.repository.BookingRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingMaintenanceService {

    private final BookingRepository bookingRepository;
    private final Clock clock;

    public BookingMaintenanceService(BookingRepository bookingRepository, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    @Transactional
    public int autoCompletePastConfirmedBookings(Long userId) {
        if (userId == null) return 0;
        LocalDateTime now = LocalDateTime.now(clock);
        return bookingRepository.completePastConfirmedBookings(userId, now);
    }
}


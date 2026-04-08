package com.example.desabackend.controller;

import com.example.desabackend.dto.BookingDto;
import com.example.desabackend.dto.CreateBookingRequestDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/users/{userId}/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@PathVariable Long userId,
            @Valid @RequestBody CreateBookingRequestDto request) {
        return bookingService.createBooking(userId, request);
    }

    @DeleteMapping("/{bookingId}")
    public BookingDto cancelBooking(@PathVariable Long userId, @PathVariable Long bookingId) {
        return bookingService.cancelBooking(userId, bookingId);
    }

    @GetMapping
    public PageResponse<BookingDto> listMyBookings(@PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size) {
        return bookingService.listMyBookings(userId, status, page, size);
    }
}

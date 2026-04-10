package com.example.desabackend.controller;

import com.example.desabackend.dto.CreateReviewRequestDto;
import com.example.desabackend.dto.ReviewSummaryDto;
import com.example.desabackend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewSummaryDto createReview(@PathVariable Long userId,
            @Valid @RequestBody CreateReviewRequestDto request) {
        return reviewService.createReview(userId, request);
    }

    @GetMapping("/booking/{bookingId}")
    public ReviewSummaryDto getReviewByBooking(@PathVariable Long userId,
            @PathVariable Long bookingId) {
        return reviewService.getReviewByBooking(userId, bookingId);
    }
}

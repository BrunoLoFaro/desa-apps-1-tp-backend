package com.example.desabackend.controller;

import com.example.desabackend.dto.ActivityDetailDto;
import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.security.AuthUtils;
import com.example.desabackend.service.ActivityCatalogService;
import com.example.desabackend.service.RecommendationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
/**
 * Read-only catalog endpoints for the mobile Home screen (activities list, featured, recommended, detail).
 *
 * Note: `/activities/recommended` supports a temporary `userId` query param for development, and can also
 * derive the user id from a Bearer JWT payload once authentication is integrated.
 */
public class ActivityController {

    private final ActivityCatalogService catalogService;
    private final RecommendationService recommendationService;

    public ActivityController(ActivityCatalogService catalogService, RecommendationService recommendationService) {
        this.catalogService = catalogService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/activities")
    public PageResponse<ActivitySummaryDto> listActivities(
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return catalogService.listActivities(page, size, destinationId, category, date, minPrice, maxPrice, false);
    }

    @GetMapping("/activities/featured")
    public PageResponse<ActivitySummaryDto> listFeatured(
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) ActivityCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return catalogService.listActivities(page, size, destinationId, category, date, minPrice, maxPrice, true);
    }    @GetMapping("/activities/recommended")
    public PageResponse<ActivitySummaryDto> listRecommended(
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) ActivityCategory category
    ) {
        Long userId = AuthUtils.getCurrentUserId();
        return recommendationService.listRecommended(userId, page, size, destinationId, category);
    }

    @GetMapping("/activities/{activityId}")
    public ActivityDetailDto getActivityDetail(
            @PathVariable Long activityId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ) {
        return catalogService.getActivityDetail(activityId, date);
    }

    @GetMapping("/categories")
    public List<String> listCategories() {
        return java.util.Arrays.stream(ActivityCategory.values())
                .map(Enum::name)
                .toList();
    }
}

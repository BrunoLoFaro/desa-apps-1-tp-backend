package com.example.desabackend.controller;

import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.security.AuthUtils;
import com.example.desabackend.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<ActivitySummaryDto>> getFavorites() {
        Long userId = AuthUtils.getCurrentUserId();
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    @PostMapping("/{activityId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long activityId) {
        Long userId = AuthUtils.getCurrentUserId();
        favoriteService.addFavorite(userId, activityId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long activityId) {
        Long userId = AuthUtils.getCurrentUserId();
        favoriteService.removeFavorite(userId, activityId);
        return ResponseEntity.ok().build();
    }
}

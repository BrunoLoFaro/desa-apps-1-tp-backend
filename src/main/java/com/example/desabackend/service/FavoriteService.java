package com.example.desabackend.service;

import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.Favorite;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.FavoriteRepository;
import com.example.desabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityCatalogService activityCatalogService;

    @Transactional
    public void addFavorite(Long userId, Long activityId) {
        if (!favoriteRepository.existsByUserIdAndActivityId(userId, activityId)) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            ActivityEntity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new NotFoundException("Activity not found"));
            Favorite favorite = new Favorite(user, activity);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(Long userId, Long activityId) {
        Favorite favorite = favoriteRepository.findByUserIdAndActivityId(userId, activityId)
                .orElseThrow(() -> new NotFoundException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public List<ActivitySummaryDto> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Long> activityIds = favorites.stream()
                .map(favorite -> favorite.getActivity().getId())
                .toList();
        return activityCatalogService.getActivitiesByIds(activityIds, userId);
    }
}

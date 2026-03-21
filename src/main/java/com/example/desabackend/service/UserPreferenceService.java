package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.repository.UserPreferredCategoryRepository;
import com.example.desabackend.repository.UserPreferredDestinationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
/**
 * Reads stored user preferences used to compute catalog recommendations.
 */
public class UserPreferenceService {

    private final UserPreferredDestinationRepository destinationRepository;
    private final UserPreferredCategoryRepository categoryRepository;

    public UserPreferenceService(
            UserPreferredDestinationRepository destinationRepository,
            UserPreferredCategoryRepository categoryRepository
    ) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Long> getPreferredDestinationIds(long userId) {
        return destinationRepository.findByUserId(userId).stream()
                .map(p -> p.getDestinationId())
                .distinct()
                .toList();
    }

    public List<ActivityCategory> getPreferredCategories(long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(p -> p.getCategory())
                .distinct()
                .toList();
    }
}

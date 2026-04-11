package com.example.desabackend.repository;

import com.example.desabackend.entity.UserPreferredCategoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User category preferences used by the recommendation ranking.
 */
public interface UserPreferredCategoryRepository extends JpaRepository<UserPreferredCategoryEntity, Long> {
    List<UserPreferredCategoryEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

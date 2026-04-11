package com.example.desabackend.repository;

import com.example.desabackend.entity.UserPreferredDestinationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User destination preferences used by the recommendation ranking.
 */
public interface UserPreferredDestinationRepository extends JpaRepository<UserPreferredDestinationEntity, Long> {
    List<UserPreferredDestinationEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

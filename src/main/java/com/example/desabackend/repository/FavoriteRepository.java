package com.example.desabackend.repository;

import com.example.desabackend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndActivityId(Long userId, Long activityId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndActivityId(Long userId, Long activityId);

    @Query("""
            SELECT f FROM Favorite f
            JOIN FETCH f.activity a
            LEFT JOIN FETCH a.guide
            LEFT JOIN FETCH a.destination
            WHERE f.user.id = :userId
            ORDER BY f.createdAt DESC
            """)
    List<Favorite> findByUserIdWithDetailsOrderByCreatedAtDesc(@Param("userId") Long userId);
}

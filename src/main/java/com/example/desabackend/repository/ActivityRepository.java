package com.example.desabackend.repository;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;

/**
 * Activity persistence + a custom query used to order "recommended" activities.
 *
 * Uses {@link EntityGraph} to eagerly load destination/guide for list endpoints (avoid N+1 with open-in-view=false).
 */
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long>, JpaSpecificationExecutor<ActivityEntity> {

    @Override
    @EntityGraph(attributePaths = {"destination", "guide"})
    Page<ActivityEntity> findAll(Specification<ActivityEntity> spec, Pageable pageable);

    @Query("""
            select a
            from ActivityEntity a
            where (:destinationId is null or a.destination.id = :destinationId)
              and (:category is null or a.category = :category)
              and (:featuredOnly = false or a.featured = true)
            order by
              (
                (case when :prefDestEmpty = true then 0 when a.destination.id in :preferredDestinationIds then 2 else 0 end)
                +
                (case when :prefCatEmpty = true then 0 when a.category in :preferredCategories then 1 else 0 end)
              ) desc,
              a.featured desc,
              (case when (select min(s.startTime) from ActivitySessionEntity s where s.activity = a and s.startTime >= :now) is null then 1 else 0 end) asc,
              (select min(s2.startTime) from ActivitySessionEntity s2 where s2.activity = a and s2.startTime >= :now) asc,
              a.id asc
            """)
    @EntityGraph(attributePaths = {"destination", "guide"})
    Page<ActivityEntity> findRecommended(
            @Param("destinationId") Long destinationId,
            @Param("category") ActivityCategory category,
            @Param("featuredOnly") boolean featuredOnly,
            @Param("preferredDestinationIds") List<Long> preferredDestinationIds,
            @Param("preferredCategories") List<ActivityCategory> preferredCategories,
            @Param("prefDestEmpty") boolean prefDestEmpty,
            @Param("prefCatEmpty") boolean prefCatEmpty,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}

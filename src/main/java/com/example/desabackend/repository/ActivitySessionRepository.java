package com.example.desabackend.repository;

import com.example.desabackend.entity.ActivitySessionEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Session ("salida") queries and aggregations used to compute catalog card price and availability efficiently.
 */
public interface ActivitySessionRepository extends JpaRepository<ActivitySessionEntity, Long> {

    interface ActivitySummaryAggregate {
        Long getActivityId();

        Long getAvailableSpots();

        java.math.BigDecimal getPrice();
    }

    @Query("""
            select s.activity.id as activityId,
                   sum(case when (s.capacity - s.bookedCount) > 0 then (s.capacity - s.bookedCount) else 0 end) as availableSpots,
                   min(coalesce(s.priceOverride, s.activity.basePrice)) as price
            from ActivitySessionEntity s
            where s.activity.id in :activityIds
              and s.startTime >= :start
              and s.startTime < :end
              and (s.capacity - s.bookedCount) > 0
              and (:minPrice is null or coalesce(s.priceOverride, s.activity.basePrice) >= :minPrice)
              and (:maxPrice is null or coalesce(s.priceOverride, s.activity.basePrice) <= :maxPrice)
            group by s.activity.id
            """)
    List<ActivitySummaryAggregate> aggregateForDate(
            @Param("activityIds") List<Long> activityIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice
    );

    @Query("""
            select s.activity.id as activityId,
                   sum(case when (s.capacity - s.bookedCount) > 0 then (s.capacity - s.bookedCount) else 0 end) as availableSpots,
                   min(coalesce(s.priceOverride, s.activity.basePrice)) as price
            from ActivitySessionEntity s
            where s.activity.id in :activityIds
              and s.startTime = (
                  select min(s2.startTime)
                  from ActivitySessionEntity s2
                  where s2.activity.id = s.activity.id
                    and s2.startTime >= :now
              )
            group by s.activity.id
            """)
    List<ActivitySummaryAggregate> aggregateForNextSession(
            @Param("activityIds") List<Long> activityIds,
            @Param("now") LocalDateTime now
    );

    @Query("""
            select s
            from ActivitySessionEntity s
            where s.activity.id = :activityId
              and s.startTime >= :start
              and s.startTime < :end
            order by s.startTime asc
            """)
    List<ActivitySessionEntity> findByActivityIdAndDay(
            @Param("activityId") Long activityId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            select s
            from ActivitySessionEntity s
            where s.activity.id = :activityId
              and s.startTime >= :now
            order by s.startTime asc
            """)
    List<ActivitySessionEntity> findFutureByActivityId(
            @Param("activityId") Long activityId,
            @Param("now") LocalDateTime now
    );
}

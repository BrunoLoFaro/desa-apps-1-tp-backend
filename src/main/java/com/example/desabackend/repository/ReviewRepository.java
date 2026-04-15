package com.example.desabackend.repository;

import com.example.desabackend.entity.ReviewEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    interface ActivityRatingAggregate {
        Long getActivityId();
        Double getAvgRating();
        Long getReviewCount();
    }

    @Query("""
            select r.activity.id as activityId, avg(r.activityRating) as avgRating, count(r.id) as reviewCount
            from ReviewEntity r
            where r.activity.id in :activityIds
            group by r.activity.id
            """)
    List<ActivityRatingAggregate> aggregateActivityRatings(@Param("activityIds") List<Long> activityIds);

    @Query("""
            select r.activity.id as activityId, avg(r.activityRating) as avgRating, count(r.id) as reviewCount
            from ReviewEntity r
            where r.activity.id = :activityId
            group by r.activity.id
            """)
    Optional<ActivityRatingAggregate> getActivityRating(@Param("activityId") Long activityId);
    boolean existsByBookingId(Long bookingId);

    Optional<ReviewEntity> findByBookingId(Long bookingId);

    Optional<ReviewEntity> findByBookingIdAndUserId(Long bookingId, Long userId);
}

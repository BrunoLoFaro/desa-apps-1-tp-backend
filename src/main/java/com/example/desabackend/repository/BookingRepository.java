package com.example.desabackend.repository;

import com.example.desabackend.entity.BookingEntity;
import com.example.desabackend.entity.BookingStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);

    Page<BookingEntity> findByUserIdAndStatusIn(Long userId, List<BookingStatus> statuses, Pageable pageable);

    Page<BookingEntity> findByUserId(Long userId, Pageable pageable);

    @Query("""
            select b from BookingEntity b
            join fetch b.session s
            join fetch s.activity a
            join fetch a.destination
            left join fetch a.guide
            where b.user.id = :userId
              and b.status = 'COMPLETED'
              and (:destinationId is null or a.destination.id = :destinationId)
              and (:startDate is null or s.startTime >= :startDate)
              and (:endDate is null or s.startTime < :endDate)
            order by s.startTime desc
            """)
    Page<BookingEntity> findHistory(
            @Param("userId") Long userId,
            @Param("destinationId") Long destinationId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable
    );

    long countByUserIdAndStatus(Long userId, BookingStatus status);
}

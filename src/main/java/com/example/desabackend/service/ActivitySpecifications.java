package com.example.desabackend.service;

import com.example.desabackend.entity.ActivityCategory;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.ActivitySessionEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

final class ActivitySpecifications {

    /**
     * JPA Specifications for the catalog list endpoint (combined filters).
     */
    private ActivitySpecifications() {
    }

    static Specification<ActivityEntity> forCatalog(
            Long destinationId,
            ActivityCategory category,
            LocalDate date,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            LocalDateTime now,
            boolean featuredOnly
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (destinationId != null) {
                predicates.add(cb.equal(root.get("destination").get("id"), destinationId));
            }

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (featuredOnly) {
                predicates.add(cb.isTrue(root.get("featured")));
            }

            if (date != null) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay();

                Join<ActivityEntity, ActivitySessionEntity> session = root.join("sessions", JoinType.INNER);
                predicates.add(cb.greaterThanOrEqualTo(session.get("startTime"), start));
                predicates.add(cb.lessThan(session.get("startTime"), end));

                Expression<Integer> diff = cb.diff(session.get("capacity"), session.get("bookedCount")).as(Integer.class);
                predicates.add(cb.greaterThan(diff, 0));

                if (minPrice != null || maxPrice != null) {
                    Expression<BigDecimal> effectivePrice = cb.<BigDecimal>coalesce(
                            session.get("priceOverride"),
                            root.get("basePrice")
                    );
                    if (minPrice != null) {
                        predicates.add(cb.greaterThanOrEqualTo(effectivePrice, minPrice));
                    }
                    if (maxPrice != null) {
                        predicates.add(cb.lessThanOrEqualTo(effectivePrice, maxPrice));
                    }
                }

                query.distinct(true);
            } else {
                // When there's no date, the UI price comes from the next session. Keep filters aligned by filtering
                // against the next future session effective price when min/max are provided.
                if (minPrice != null || maxPrice != null) {
                    final LocalDateTime effectiveNow = now == null ? LocalDateTime.now() : now;

                    Join<ActivityEntity, ActivitySessionEntity> session = root.join("sessions", JoinType.INNER);

                    var minStartTime = query.subquery(LocalDateTime.class);
                    var s2 = minStartTime.from(ActivitySessionEntity.class);
                    var s2StartTime = s2.get("startTime").as(LocalDateTime.class);
                    minStartTime.select(cb.least(s2StartTime))
                            .where(
                                    cb.equal(s2.get("activity"), root),
                                    cb.greaterThanOrEqualTo(s2StartTime, effectiveNow)
                            );

                    predicates.add(cb.equal(session.get("startTime"), minStartTime));

                    Expression<BigDecimal> effectivePrice = cb.<BigDecimal>coalesce(
                            session.get("priceOverride"),
                            root.get("basePrice")
                    );

                    if (minPrice != null) {
                        predicates.add(cb.greaterThanOrEqualTo(effectivePrice, minPrice));
                    }
                    if (maxPrice != null) {
                        predicates.add(cb.lessThanOrEqualTo(effectivePrice, maxPrice));
                    }

                    query.distinct(true);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

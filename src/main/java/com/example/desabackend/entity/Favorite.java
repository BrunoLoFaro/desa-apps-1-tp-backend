package com.example.desabackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorites_user_activity", columnNames = {"user_id", "activity_id"})
)

public class Favorite {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityEntity activity;

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter
    @Getter
    @Column(name = "snapshot_price", precision = 10, scale = 2)
    private BigDecimal snapshotPrice;

    @Setter
    @Getter
    @Column(name = "snapshot_slots")
    private Integer snapshotSlots;

    public Favorite() {
    }

    public Favorite(UserEntity user, ActivityEntity activity, BigDecimal snapshotPrice, int snapshotSlots) {
        this.user = user;
        this.activity = activity;
        this.createdAt = LocalDateTime.now();
        this.snapshotPrice = snapshotPrice;
        this.snapshotSlots = snapshotSlots;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}

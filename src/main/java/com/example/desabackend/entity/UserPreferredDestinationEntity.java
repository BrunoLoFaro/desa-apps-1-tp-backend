package com.example.desabackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "user_preferred_destinations",
        indexes = {
                @Index(name = "idx_upd_user_id", columnList = "user_id"),
                @Index(name = "idx_upd_dest_id", columnList = "destination_id")
        }
)
/**
 * User preference link for recommendations (kept independent from the auth user table).
 */
public class UserPreferredDestinationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "destination_id", nullable = false)
    private Long destinationId;
}

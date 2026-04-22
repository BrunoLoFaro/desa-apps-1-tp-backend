package com.example.desabackend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

        @Column(name = "first_name", nullable = false, length = 80)
        private String firstName;

        @Column(name = "last_name", nullable = false, length = 80)
        private String lastName;

        @Column(nullable = true, length = 20)
        private String dni;

    @Column(length = 30)
    private String phone;


    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @jakarta.persistence.Lob
    @Column(name = "profile_photo_blob")
    private byte[] profilePhotoBlob;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (enabled == null) enabled = true;
    }
}

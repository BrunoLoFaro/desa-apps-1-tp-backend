package com.example.desabackend.entity;

import com.example.desabackend.dto.NewsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "news")
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String fullContent;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NewsType type;

    @Column
    private Long relatedActivityId;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column
    private LocalDateTime validUntil;

    @Column(length = 100)
    private String ctaText;

    @Column(length = 500)
    private String ctaLink;
}

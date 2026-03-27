package com.example.desabackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "activities")
/**
 * Core activity/tour entity shown in the catalog.
 *
 * Availability and "next session" data is derived from {@link ActivitySessionEntity}.
 */
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String includesText;

    @Column(length = 255)
    private String meetingPoint;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(length = 10)
    private String language;    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String cancellationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ActivityCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, length = 5)
    private String currency;

    @Column(nullable = false)
    private boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private DestinationEntity destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private GuideEntity guide;

    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    private List<ActivitySessionEntity> sessions = new ArrayList<>();
}

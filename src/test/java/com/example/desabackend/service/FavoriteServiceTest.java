package com.example.desabackend.service;

import com.example.desabackend.dto.ActivitySummaryDto;
import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.dto.FavoriteActivityDto;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.Favorite;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.ActivitySessionRepository;
import com.example.desabackend.repository.FavoriteRepository;
import com.example.desabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActivityRepository activityRepository;
    @Mock private ActivityCatalogService activityCatalogService;
    @Mock private ActivitySessionRepository sessionRepository;

    private FavoriteService service;

    @BeforeEach
    void setUp() {
        service = new FavoriteService(favoriteRepository, userRepository, activityRepository, activityCatalogService, sessionRepository);
    }

    // ── addFavorite ────────────────────────────────────────────────────────────

    @Test
    void addFavorite_newEntry_savesWithSnapshot() {
        UserEntity user = makeUser(1L);
        ActivityEntity activity = makeActivity(10L, new BigDecimal("100.00"));
        ActivitySummaryDto summary = makeSummary(10L, new BigDecimal("100.00"), 5);

        when(favoriteRepository.existsByUserIdAndActivityId(1L, 10L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(activityRepository.findById(10L)).thenReturn(Optional.of(activity));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L)).thenReturn(List.of(summary));

        service.addFavorite(1L, 10L);

        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        Favorite saved = captor.getValue();
        assertThat(saved.getSnapshotPrice()).isEqualByComparingTo("100.00");
        assertThat(saved.getSnapshotSlots()).isEqualTo(5);
    }

    @Test
    void addFavorite_alreadyExists_doesNotSaveDuplicate() {
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 10L)).thenReturn(true);
        service.addFavorite(1L, 10L);
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_userNotFound_throwsNotFoundException() {
        when(favoriteRepository.existsByUserIdAndActivityId(anyLong(), anyLong())).thenReturn(false);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addFavorite(99L, 10L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addFavorite_activityNotFound_throwsNotFoundException() {
        when(favoriteRepository.existsByUserIdAndActivityId(anyLong(), anyLong())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(makeUser(1L)));
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addFavorite(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ── removeFavorite ─────────────────────────────────────────────────────────

    @Test
    void removeFavorite_exists_deletesEntry() {
        Favorite fav = new Favorite();
        when(favoriteRepository.findByUserIdAndActivityId(1L, 10L)).thenReturn(Optional.of(fav));
        service.removeFavorite(1L, 10L);
        verify(favoriteRepository).delete(fav);
    }

    @Test
    void removeFavorite_notFound_throwsNotFoundException() {
        when(favoriteRepository.findByUserIdAndActivityId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.removeFavorite(1L, 10L))
                .isInstanceOf(NotFoundException.class);
    }

    // ── getFavorites ───────────────────────────────────────────────────────────

    @Test
    void getFavorites_empty_returnsEmptyList() {
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        assertThat(service.getFavorites(1L)).isEmpty();
    }

    @Test
    void getFavorites_priceUnchanged_setsFlagFalse() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 5);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 5)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).hasPriceChange()).isFalse();
    }

    @Test
    void getFavorites_priceChanged_setsFlagTrue() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 5);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("120.00"), 5)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).hasPriceChange()).isTrue();
    }

    @Test
    void getFavorites_slotsIncreased_setsAvailabilityChangeFlagTrue() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 0);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 3)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).hasAvailabilityChange()).isTrue();
    }

    @Test
    void getFavorites_slotsDecreased_setsAvailabilityChangeFlagFalse() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 10);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 2)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).hasAvailabilityChange()).isFalse();
    }

    @Test
    void getFavorites_noSnapshot_neitherFlagSet() {
        Favorite fav = makeFavoriteEntity(10L, null, null);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 5)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).hasPriceChange()).isFalse();
        assertThat(result.get(0).hasAvailabilityChange()).isFalse();
    }

    @Test
    void getFavorites_zeroSpots_availableSpotsZero() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 0);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 0)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any())).thenReturn(List.of());

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).availableSpots()).isZero();
    }

    @Test
    void getFavorites_nextSessionDate_populatedInStartDate() {
        Favorite fav = makeFavoriteEntity(10L, new BigDecimal("100.00"), 5);
        LocalDateTime nextDate = LocalDateTime.of(2026, 5, 10, 9, 0);
        when(favoriteRepository.findByUserIdWithDetailsOrderByCreatedAtDesc(1L)).thenReturn(List.of(fav));
        when(activityCatalogService.getActivitiesByIds(List.of(10L), 1L))
                .thenReturn(List.of(makeSummary(10L, new BigDecimal("100.00"), 5)));
        when(sessionRepository.findNextSessionDateByActivityIds(any(), any()))
                .thenReturn(List.of(makeNextSessionDate(10L, nextDate)));

        List<FavoriteActivityDto> result = service.getFavorites(1L);
        assertThat(result.get(0).startDate()).isEqualTo(nextDate.toLocalDate());
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private UserEntity makeUser(Long id) {
        UserEntity u = new UserEntity();
        u.setId(id);
        return u;
    }

    private ActivityEntity makeActivity(Long id, BigDecimal basePrice) {
        ActivityEntity a = new ActivityEntity();
        a.setId(id);
        a.setBasePrice(basePrice);
        return a;
    }

    private ActivitySummaryDto makeSummary(Long id, BigDecimal price, int availableSpots) {
        return new ActivitySummaryDto(id, "Activity " + id, null,
                new DestinationDto(1L, "Bariloche"), null,
                60, price, "ARS", availableSpots, null, 0L, true);
    }

    private Favorite makeFavoriteEntity(Long activityId, BigDecimal snapshotPrice, Integer snapshotSlots) {
        ActivityEntity activity = new ActivityEntity();
        activity.setId(activityId);
        Favorite fav = new Favorite();
        fav.setActivity(activity);
        fav.setSnapshotPrice(snapshotPrice);
        fav.setSnapshotSlots(snapshotSlots);
        return fav;
    }

    private ActivitySessionRepository.NextSessionDateProjection makeNextSessionDate(Long activityId, LocalDateTime date) {
        return new ActivitySessionRepository.NextSessionDateProjection() {
            public Long getActivityId() { return activityId; }
            public LocalDateTime getNextSessionDate() { return date; }
        };
    }
}

package com.example.desabackend.service;

import com.example.desabackend.dto.NewsDto;
import com.example.desabackend.dto.NewsDetailDto;
import com.example.desabackend.dto.NewsType;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.entity.ActivityEntity;
import com.example.desabackend.entity.NewsEntity;
import com.example.desabackend.exception.NotFoundException;
import com.example.desabackend.repository.ActivityRepository;
import com.example.desabackend.repository.NewsRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service to fetch news, offers, and featured destinations from the database.
 * - NEWS: stored in the news table
 * - OFFER: generated dynamically from free and discounted activities
 * - FEATURED_DESTINATION: generated dynamically from featured activities
 */
@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;
    private final ActivityRepository activityRepository;

    public NewsService(NewsRepository newsRepository, ActivityRepository activityRepository) {
        this.newsRepository = newsRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Fetch paginated list of news.
     * - Promotions (OFFER) from free and discounted activities
     * - News (NEWS) from the news table
     * - Featured destinations (FEATURED_DESTINATION) from featured activities
     * Results are cached for 5 minutes.
     */
    @Cacheable(value = "newsList", key = "'list_' + #page + '_' + #size")
    public PageResponse<NewsDto> listNews(Integer page, Integer size) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));

        // Get all items from database
        List<NewsDto> promotions = getPromotionalActivities();
        List<NewsDto> news = getNewsFromDatabase();
        List<NewsDto> featured = getFeaturedDestinations();

        // Combine: offers first, then news, then featured destinations
        List<NewsDto> allItems = new ArrayList<>();
        allItems.addAll(promotions);
        allItems.addAll(news);
        allItems.addAll(featured);

        // Apply pagination
        int start = safePage * safeSize;
        int end = Math.min(start + safeSize, allItems.size());
        List<NewsDto> paginatedItems = start < allItems.size()
                ? allItems.subList(start, end)
                : List.of();

        return new PageResponse<>(
                paginatedItems,
                safePage,
                safeSize,
                allItems.size(),
                (int) Math.ceil((double) allItems.size() / safeSize)
        );
    }

    /**
     * Fetch detailed information for a specific news item.
     * Results are cached for 5 minutes.
     */
    @Cacheable(value = "newsDetail", key = "#newsId")
    public NewsDetailDto getNewsDetail(Long newsId) {
        NewsEntity entity = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found: " + newsId));

        String relatedActivityName = null;
        if (entity.getRelatedActivityId() != null) {
            try {
                ActivityEntity activity = activityRepository.findById(entity.getRelatedActivityId()).orElse(null);
                if (activity != null) {
                    relatedActivityName = activity.getName();
                }
            } catch (Exception e) {
                logger.warn("Could not find related activity {} for news {}", entity.getRelatedActivityId(), newsId);
            }
        }

        return new NewsDetailDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getFullContent() != null ? entity.getFullContent() : entity.getDescription(),
                entity.getImageUrl(),
                entity.getType(),
                entity.getRelatedActivityId(),
                relatedActivityName,
                entity.getPublishedAt(),
                entity.getValidUntil(),
                entity.getCtaText(),
                entity.getCtaLink()
        );
    }

    /**
     * Returns news articles from the database, ordered by published date.
     */
    private List<NewsDto> getNewsFromDatabase() {
        try {
            List<NewsEntity> newsEntities = newsRepository.findByTypeOrderByPublishedAtDesc(NewsType.NEWS);
            logger.info("News articles from database: {}", newsEntities.size());

            List<NewsDto> items = new ArrayList<>();
            for (NewsEntity entity : newsEntities) {
                items.add(new NewsDto(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getImageUrl(),
                        entity.getType(),
                        entity.getRelatedActivityId(),
                        entity.getPublishedAt(),
                        entity.getValidUntil()
                ));
            }
            return items;
        } catch (Exception e) {
            logger.error("Error fetching news from database", e);
            return new ArrayList<>();
        }
    }

    /**
     * Returns free activities and activities with discounts from database as OFFER type promotions.
     */
    private List<NewsDto> getPromotionalActivities() {
        try {
            List<ActivityEntity> allActivities = activityRepository.findAll();
            logger.info("Total activities in database: {}", allActivities.size());

            List<ActivityEntity> promotionalActivities = allActivities.stream()
                    .filter(activity -> {
                        boolean isFree = activity.getBasePrice().compareTo(BigDecimal.ZERO) == 0;
                        boolean hasDiscount = activity.getDiscountPercentage() != null && activity.getDiscountPercentage() > 0;
                        boolean isPromotional = isFree || hasDiscount;

                        if (isPromotional) {
                            String reason = isFree ? "FREE" : "DISCOUNT " + activity.getDiscountPercentage() + "%";
                            logger.info("Promotional activity found: {} (price: {}, reason: {})", activity.getName(), activity.getBasePrice(), reason);
                        }
                        return isPromotional;
                    })
                    .toList();

            logger.info("Promotional activities count: {}", promotionalActivities.size());

            List<NewsDto> items = new ArrayList<>();
            for (ActivityEntity activity : promotionalActivities) {
                String description;
                if (activity.getBasePrice().compareTo(BigDecimal.ZERO) == 0) {
                    description = "¡GRATIS! " + activity.getName();
                } else {
                    description = activity.getDiscountPercentage() + "% OFF - " + activity.getName();
                }

                items.add(new NewsDto(
                        activity.getId(),
                        activity.getName(),
                        description,
                        activity.getImageUrl(),
                        NewsType.OFFER,
                        activity.getId(),
                        LocalDateTime.now(),
                        null
                ));
            }
            return items;
        } catch (Exception e) {
            logger.error("Error fetching promotional activities from database", e);
            return new ArrayList<>();
        }
    }

    /**
     * Returns featured activities as FEATURED_DESTINATION type items.
     */
    private List<NewsDto> getFeaturedDestinations() {
        try {
            List<ActivityEntity> allActivities = activityRepository.findAll();

            List<ActivityEntity> featuredActivities = allActivities.stream()
                    .filter(activity -> activity.isFeatured())
                    .toList();

            logger.info("Featured activities count: {}", featuredActivities.size());

            List<NewsDto> items = new ArrayList<>();
            for (ActivityEntity activity : featuredActivities) {
                String description = activity.getDestination() != null
                        ? activity.getDestination().getCity() + " - " + activity.getName()
                        : activity.getName();

                items.add(new NewsDto(
                        activity.getId(),
                        activity.getName(),
                        description,
                        activity.getImageUrl(),
                        NewsType.FEATURED_DESTINATION,
                        activity.getId(),
                        LocalDateTime.now(),
                        null
                ));
            }
            return items;
        } catch (Exception e) {
            logger.error("Error fetching featured destinations from database", e);
            return new ArrayList<>();
        }
    }
}

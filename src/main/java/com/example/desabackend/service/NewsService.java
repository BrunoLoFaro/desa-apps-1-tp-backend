package com.example.desabackend.service;

import com.example.desabackend.dto.NewsDto;
import com.example.desabackend.dto.NewsDetailDto;
import com.example.desabackend.dto.NewsType;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service to fetch news, offers, and featured destinations from XploreNow external API.
 * Implements caching to avoid overloading the external API.
 */
@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String newsEndpoint;
    private final Duration timeout;

    public NewsService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${xplorenow.api.base-url}") String baseUrl,
            @Value("${xplorenow.api.news-endpath}") String newsEndpoint,
            @Value("${xplorenow.api.timeout:5000}") int timeoutMs
    ) {
        this.objectMapper = objectMapper;
        this.newsEndpoint = newsEndpoint;
        this.timeout = Duration.ofMillis(timeoutMs);
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Fetch paginated list of news from XploreNow API.
     * Results are cached for 5 minutes (configurable via cache.news.ttl).
     */
    @Cacheable(value = "newsList", key = "'list_' + #page + '_' + #size")
    public PageResponse<NewsDto> listNews(Integer page, Integer size) {
        int safePage = page == null ? 0 : Math.max(0, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));

        try {
            String response = webClient.get()
                    .uri(newsEndpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(timeout);

            return parseNewsListResponse(response, safePage, safeSize);
        } catch (WebClientResponseException e) {
            logger.error("Error fetching news from XploreNow API: {}", e.getStatusCode());
            throw new RuntimeException("Failed to fetch news from external API", e);
        } catch (Exception e) {
            logger.error("Unexpected error fetching news", e);
            throw new RuntimeException("Unexpected error fetching news", e);
        }
    }

    /**
     * Fetch detailed information for a specific news item.
     * Results are cached for 5 minutes.
     */
    @Cacheable(value = "newsDetail", key = "#newsId")
    public NewsDetailDto getNewsDetail(Long newsId) {
        try {
            String response = webClient.get()
                    .uri(newsEndpoint + "/{id}", newsId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(timeout);

            return parseNewsDetailResponse(response, newsId);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new NotFoundException("News not found: " + newsId);
            }
            logger.error("Error fetching news detail from XploreNow API: {}", e.getStatusCode());
            throw new RuntimeException("Failed to fetch news detail from external API", e);
        } catch (Exception e) {
            logger.error("Unexpected error fetching news detail", e);
            throw new RuntimeException("Unexpected error fetching news detail", e);
        }
    }

    /**
     * Parse the JSON response from XploreNow API for news list.
     * Expected format:
     * {
     *   "items": [
     *     {
     *       "id": 1,
     *       "title": "...",
     *       "description": "...",
     *       "imageUrl": "...",
     *       "type": "NEWS|OFFER|FEATURED_DESTINATION",
     *       "relatedActivityId": 123,
     *       "publishedAt": "2024-01-01T00:00:00",
     *       "validUntil": "2024-12-31T23:59:59"
     *     }
     *   ],
     *   "totalElements": 100
     * }
     */
    private PageResponse<NewsDto> parseNewsListResponse(String jsonResponse, int page, int size) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = root.get("items");
            long totalElements = root.has("totalElements") ? root.get("totalElements").asLong() : 0;

            List<NewsDto> items = new ArrayList<>();
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    items.add(parseNewsItem(itemNode));
                }
            }

            int totalPages = (int) Math.ceil((double) totalElements / size);

            return new PageResponse<>(
                    items,
                    page,
                    size,
                    totalElements,
                    totalPages
            );
        } catch (Exception e) {
            logger.error("Error parsing news list response", e);
            throw new RuntimeException("Failed to parse news response", e);
        }
    }

    /**
     * Parse the JSON response from XploreNow API for news detail.
     * Expected format:
     * {
     *   "id": 1,
     *   "title": "...",
     *   "description": "...",
     *   "fullContent": "...",
     *   "imageUrl": "...",
     *   "type": "NEWS|OFFER|FEATURED_DESTINATION",
     *   "relatedActivityId": 123,
     *   "relatedActivityName": "...",
     *   "publishedAt": "2024-01-01T00:00:00",
     *   "validUntil": "2024-12-31T23:59:59",
     *   "ctaText": "...",
     *   "ctaLink": "..."
     * }
     */
    private NewsDetailDto parseNewsDetailResponse(String jsonResponse, Long newsId) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return parseNewsDetailItem(root);
        } catch (Exception e) {
            logger.error("Error parsing news detail response", e);
            throw new RuntimeException("Failed to parse news detail response", e);
        }
    }

    private NewsDto parseNewsItem(JsonNode itemNode) {
        return new NewsDto(
                itemNode.get("id").asLong(),
                itemNode.get("title").asText(),
                itemNode.get("description").asText(),
                itemNode.has("imageUrl") ? itemNode.get("imageUrl").asText() : null,
                parseNewsType(itemNode.get("type").asText()),
                itemNode.has("relatedActivityId") ? itemNode.get("relatedActivityId").asLong() : null,
                parseDateTime(itemNode.get("publishedAt")),
                itemNode.has("validUntil") ? parseDateTime(itemNode.get("validUntil")) : null
        );
    }

    private NewsDetailDto parseNewsDetailItem(JsonNode itemNode) {
        return new NewsDetailDto(
                itemNode.get("id").asLong(),
                itemNode.get("title").asText(),
                itemNode.get("description").asText(),
                itemNode.has("fullContent") ? itemNode.get("fullContent").asText() : itemNode.get("description").asText(),
                itemNode.has("imageUrl") ? itemNode.get("imageUrl").asText() : null,
                parseNewsType(itemNode.get("type").asText()),
                itemNode.has("relatedActivityId") ? itemNode.get("relatedActivityId").asLong() : null,
                itemNode.has("relatedActivityName") ? itemNode.get("relatedActivityName").asText() : null,
                parseDateTime(itemNode.get("publishedAt")),
                itemNode.has("validUntil") ? parseDateTime(itemNode.get("validUntil")) : null,
                itemNode.has("ctaText") ? itemNode.get("ctaText").asText() : null,
                itemNode.has("ctaLink") ? itemNode.get("ctaLink").asText() : null
        );
    }

    private NewsType parseNewsType(String typeStr) {
        try {
            return NewsType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown news type: {}, defaulting to NEWS", typeStr);
            return NewsType.NEWS;
        }
    }

    private LocalDateTime parseDateTime(JsonNode dateTimeNode) {
        if (dateTimeNode == null || dateTimeNode.isNull()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeNode.asText(), DATE_FORMATTER);
        } catch (Exception e) {
            logger.warn("Failed to parse datetime: {}", dateTimeNode.asText());
            return null;
        }
    }
}

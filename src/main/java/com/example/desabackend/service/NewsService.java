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
     * Falls back to mock data if external API fails.
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
            logger.warn("Error fetching news from XploreNow API: {}, using mock data", e.getStatusCode());
            return getMockNewsList(safePage, safeSize);
        } catch (Exception e) {
            logger.warn("Unexpected error fetching news, using mock data: {}", e.getMessage());
            return getMockNewsList(safePage, safeSize);
        }
    }

    /**
     * Fetch detailed information for a specific news item.
     * Results are cached for 5 minutes.
     * Falls back to mock data if external API fails.
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
            logger.warn("Error fetching news detail from XploreNow API: {}, using mock data", e.getStatusCode());
            return getMockNewsDetail(newsId);
        } catch (Exception e) {
            logger.warn("Unexpected error fetching news detail, using mock data: {}", e.getMessage());
            return getMockNewsDetail(newsId);
        }
    }

    /**
     * Returns mock news data for testing when external API is unavailable.
     */
    private PageResponse<NewsDto> getMockNewsList(int page, int size) {
        List<NewsDto> mockItems = new ArrayList<>();
        
        mockItems.add(new NewsDto(
                1L,
                "¡Nueva ruta de senderismo en la Patagonia!",
                "Descubre los paisajes más impresionantes de la Patagonia con nuestra nueva ruta guiada.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                NewsType.NEWS,
                null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(30)
        ));
        
        mockItems.add(new NewsDto(
                2L,
                "50% de descuento en excursiones a Bariloche",
                "Aprovecha esta oferta especial para visitar Bariloche con precio reducido.",
                "https://images.unsplash.com/photo-1531310197838-673665d45428?w=800",
                NewsType.OFFER,
                10L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(7)
        ));
        
        mockItems.add(new NewsDto(
                3L,
                "Destacado: Cataratas del Iguazú",
                "Una de las maravillas naturales del mundo te espera. Reserva ahora tu viaje.",
                "https://images.unsplash.com/photo-1591175493419-990a2d9bfe9a?w=800",
                NewsType.FEATURED_DESTINATION,
                15L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(60)
        ));
        
        mockItems.add(new NewsDto(
                4L,
                "Nuevo tour gastronómico en Mendoza",
                "Disfruta de los mejores vinos y comidas típicas de la región.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                NewsType.NEWS,
                null,
                LocalDateTime.now().minusDays(5),
                null
        ));
        
        mockItems.add(new NewsDto(
                5L,
                "Pack familiar: 2x1 en parques temáticos",
                "Lleva a tu familia a divertirse con esta oferta exclusiva por tiempo limitado.",
                "https://images.unsplash.com/photo-1569949381669-ecf31ae8e613?w=800",
                NewsType.OFFER,
                20L,
                LocalDateTime.now().minusHours(12),
                LocalDateTime.now().plusDays(3)
        ));

        int totalElements = mockItems.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return new PageResponse<>(mockItems, page, size, totalElements, totalPages);
    }

    /**
     * Returns mock news detail for testing when external API is unavailable.
     */
    private NewsDetailDto getMockNewsDetail(Long newsId) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newsId.intValue()) {
            case 1:
                return new NewsDetailDto(
                        1L,
                        "¡Nueva ruta de senderismo en la Patagonia!",
                        "Descubre los paisajes más impresionantes de la Patagonia con nuestra nueva ruta guiada.",
                        "Hemos lanzado una emocionante nueva ruta de senderismo que te llevará a través de los paisajes más espectaculares de la Patagonia. Esta ruta de 3 días incluye caminatas por glaciares, lagos cristalinos y montañas imponentes. Contarás con guías expertos, equipo de alta calidad y alojamiento en refugios confortables. No te pierdas esta oportunidad única de explorar uno de los destinos más impresionantes del mundo.",
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                        NewsType.NEWS,
                        null,
                        null,
                        now.minusDays(2),
                        now.plusDays(30),
                        "Reservar ahora",
                        null
                );
            case 2:
                return new NewsDetailDto(
                        2L,
                        "50% de descuento en excursiones a Bariloche",
                        "Aprovecha esta oferta especial para visitar Bariloche con precio reducido.",
                        "Por tiempo limitado, ofrecemos un 50% de descuento en todas nuestras excursiones a Bariloche. Incluye transporte, alojamiento en hotel 4 estrellas y visitas a los principales puntos turísticos: Cerro Catedral, Lago Nahuel Huapi, Circuito Chico y más. Esta oferta es válida hasta fin de mes. ¡No te la pierdas!",
                        "https://images.unsplash.com/photo-1531310197838-673665d45428?w=800",
                        NewsType.OFFER,
                        10L,
                        "Excursión a Bariloche",
                        now.minusDays(1),
                        now.plusDays(7),
                        "Ver oferta",
                        null
                );
            case 3:
                return new NewsDetailDto(
                        3L,
                        "Destacado: Cataratas del Iguazú",
                        "Una de las maravillas naturales del mundo te espera. Reserva ahora tu viaje.",
                        "Las Cataratas del Iguazú son una de las maravillas naturales del mundo y un destino que debes visitar al menos una vez en la vida. Nuestro paquete incluye vuelos, alojamiento en hotel frente a las cataratas, visitas guiadas a ambos lados (argentino y brasileño), y excursiones en bote por debajo de las cascadas. Una experiencia inolvidable te espera.",
                        "https://images.unsplash.com/photo-1591175493419-990a2d9bfe9a?w=800",
                        NewsType.FEATURED_DESTINATION,
                        15L,
                        "Tour a Cataratas del Iguazú",
                        now,
                        now.plusDays(60),
                        "Reservar viaje",
                        null
                );
            default:
                return new NewsDetailDto(
                        newsId,
                        "Noticia de prueba",
                        "Esta es una noticia de prueba para demostrar la funcionalidad.",
                        "Contenido completo de la noticia de prueba. Aquí puedes ver más detalles sobre la noticia cuando la API externa no está disponible.",
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                        NewsType.NEWS,
                        null,
                        null,
                        now,
                        null,
                        null,
                        null
                );
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

package com.example.desabackend.controller;

import com.example.desabackend.dto.NewsDetailDto;
import com.example.desabackend.dto.NewsDto;
import com.example.desabackend.dto.PageResponse;
import com.example.desabackend.service.NewsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
/**
 * News endpoints for the mobile Home screen.
 * Fetches news, offers, and featured destinations from the database.
 */
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Get paginated list of news, offers, and featured destinations.
     * Results are cached for 5 minutes to avoid overloading the external API.
     *
     * @param page Page number (0-indexed, default: 0)
     * @param size Page size (1-100, default: 10)
     * @return Paginated list of news items
     */
    @GetMapping("/news")
    public PageResponse<NewsDto> listNews(
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) @Max(100) Integer size
    ) {
        return newsService.listNews(page, size);
    }

    /**
     * Get detailed information for a specific news item.
     * Results are cached for 5 minutes.
     *
     * @param newsId The ID of the news item
     * @return Detailed news information
     */
    @GetMapping("/news/{newsId}")
    public NewsDetailDto getNewsDetail(@PathVariable Long newsId) {
        return newsService.getNewsDetail(newsId);
    }
}

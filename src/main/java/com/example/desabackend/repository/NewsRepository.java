package com.example.desabackend.repository;

import com.example.desabackend.dto.NewsType;
import com.example.desabackend.entity.NewsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findByTypeOrderByPublishedAtDesc(NewsType type);
    List<NewsEntity> findAllByOrderByPublishedAtDesc();
}

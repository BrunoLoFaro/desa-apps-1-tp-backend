package com.example.desabackend.repository;

import com.example.desabackend.entity.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Guide persistence (referenced by activities).
 */
public interface GuideRepository extends JpaRepository<GuideEntity, Long> {
}

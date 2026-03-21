package com.example.desabackend.repository;

import com.example.desabackend.entity.DestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Destination persistence for filter lists.
 */
public interface DestinationRepository extends JpaRepository<DestinationEntity, Long> {
}

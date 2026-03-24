package com.example.desabackend.service;

import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.repository.DestinationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
/**
 * Read-only destination list service (used by filter UI).
 */
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    public List<DestinationDto> listDestinations() {
        return destinationRepository.findAll().stream()
                .map(d -> new DestinationDto(d.getId(), d.getName()))
                .toList();
    }
}

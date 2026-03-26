package com.example.desabackend.controller;

import com.example.desabackend.dto.DestinationDto;
import com.example.desabackend.service.DestinationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
/**
 * Auxiliary endpoints used by the mobile client to populate catalog filters (destinations list).
 */
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/destinations")
    public List<DestinationDto> listDestinations() {
        return destinationService.listDestinations();
    }
}

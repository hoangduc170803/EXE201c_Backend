package com.stayease.controller;

import com.stayease.dto.response.AmenityResponse;
import com.stayease.dto.response.ApiResponse;
import com.stayease.enums.AmenityCategory;
import com.stayease.repository.AmenityRepository;
import com.stayease.utils.PropertyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/amenities")
@Tag(name = "Amenities", description = "Amenity management API")
public class AmenityController {
    
    private final AmenityRepository amenityRepository;
    private final PropertyMapper propertyMapper;
    
    public AmenityController(AmenityRepository amenityRepository, PropertyMapper propertyMapper) {
        this.amenityRepository = amenityRepository;
        this.propertyMapper = propertyMapper;
    }
    
    @GetMapping
    @Operation(summary = "Get all amenities")
    public ResponseEntity<ApiResponse<List<AmenityResponse>>> getAllAmenities() {
        List<AmenityResponse> amenities = amenityRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(propertyMapper::toAmenityResponse)
                .collect(Collectors.toList());
        log.info("Get all amenities"+amenities);
        return ResponseEntity.ok(ApiResponse.success(amenities));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get amenities by category")
    public ResponseEntity<ApiResponse<List<AmenityResponse>>> getAmenitiesByCategory(
            @PathVariable AmenityCategory category) {
        List<AmenityResponse> amenities = amenityRepository.findByCategoryAndIsActiveTrueOrderByNameAsc(category)
                .stream()
                .map(propertyMapper::toAmenityResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(amenities));
    }
}


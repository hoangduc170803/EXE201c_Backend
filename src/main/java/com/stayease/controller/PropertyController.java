package com.stayease.controller;

import com.stayease.controller.request.CreatePropertyRequest;
import com.stayease.controller.response.ApiResponse;
import com.stayease.controller.response.PageResponse;
import com.stayease.controller.response.PropertyResponse;
import com.stayease.service.CustomUserDetailsService.UserPrincipal;
import com.stayease.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Property management API")
public class PropertyController {
    
    private final PropertyService propertyService;
    
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }
    
    @GetMapping
    @Operation(summary = "Get all properties")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<PropertyResponse> properties = propertyService.getAllActiveProperties(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID")
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyById(@PathVariable Long id) {
        PropertyResponse property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(ApiResponse.success(property));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search properties")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> searchProperties(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<PropertyResponse> properties = propertyService.searchProperties(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Get properties by city")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getPropertiesByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<PropertyResponse> properties = propertyService.getPropertiesByCity(city, page, size);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get properties by category")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getPropertiesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<PropertyResponse> properties = propertyService.getPropertiesByCategory(categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured properties")
    public ResponseEntity<ApiResponse<List<PropertyResponse>>> getFeaturedProperties(
            @RequestParam(defaultValue = "8") int limit) {
        List<PropertyResponse> properties = propertyService.getFeaturedProperties(limit);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/host/{hostId}")
    @Operation(summary = "Get host properties")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getHostProperties(
            @PathVariable Long hostId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<PropertyResponse> properties = propertyService.getHostProperties(hostId, page, size);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get my properties")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getMyProperties(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<PropertyResponse> properties = propertyService.getHostProperties(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/cities")
    @Operation(summary = "Get distinct cities")
    public ResponseEntity<ApiResponse<List<String>>> getDistinctCities() {
        List<String> cities = propertyService.getDistinctCities();
        return ResponseEntity.ok(ApiResponse.success(cities));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Create property")
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(
            @Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse property = propertyService.createProperty(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property created", property));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Delete property")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok(ApiResponse.success("Property deleted", null));
    }
}


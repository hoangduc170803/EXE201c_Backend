package com.stayease.service;

import com.stayease.controller.request.CreatePropertyRequest;
import com.stayease.controller.response.PageResponse;
import com.stayease.controller.response.PropertyResponse;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.exception.UnauthorizedException;
import com.stayease.model.*;
import com.stayease.model.Property.PropertyStatus;
import com.stayease.repository.*;
import com.stayease.utils.PropertyMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityRepository amenityRepository;
    private final UserService userService;
    private final PropertyMapper propertyMapper;
    
    public PropertyService(PropertyRepository propertyRepository,
                           CategoryRepository categoryRepository,
                           AmenityRepository amenityRepository,
                           UserService userService,
                           PropertyMapper propertyMapper) {
        this.propertyRepository = propertyRepository;
        this.categoryRepository = categoryRepository;
        this.amenityRepository = amenityRepository;
        this.userService = userService;
        this.propertyMapper = propertyMapper;
    }
    
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", id));
        
        propertyRepository.incrementViewCount(id);
        return propertyMapper.toResponse(property);
    }
    
    public PageResponse<PropertyResponse> getAllActiveProperties(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Property> propertyPage = propertyRepository.findByStatusAndIsDeletedFalse(PropertyStatus.ACTIVE, pageable);
        
        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(propertyPage, content);
    }
    
    public PageResponse<PropertyResponse> searchProperties(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Property> propertyPage = propertyRepository.searchProperties(keyword, PropertyStatus.ACTIVE, pageable);
        
        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(propertyPage, content);
    }
    
    public PageResponse<PropertyResponse> getPropertiesByCity(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Property> propertyPage = propertyRepository.findByCityIgnoreCaseAndStatusAndIsDeletedFalse(
                city, PropertyStatus.ACTIVE, pageable);
        
        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(propertyPage, content);
    }
    
    public PageResponse<PropertyResponse> getPropertiesByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Property> propertyPage = propertyRepository.findByCategoryIdAndStatusAndIsDeletedFalse(
                categoryId, PropertyStatus.ACTIVE, pageable);
        
        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(propertyPage, content);
    }
    
    public List<PropertyResponse> getFeaturedProperties(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return propertyRepository.findFeaturedProperties(pageable).stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public PageResponse<PropertyResponse> getHostProperties(Long hostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Property> propertyPage = propertyRepository.findByHostIdAndIsDeletedFalse(hostId, pageable);
        
        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(propertyPage, content);
    }
    
    @Transactional
    public PropertyResponse createProperty(CreatePropertyRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Property property = Property.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pricePerNight(request.getPricePerNight())
                .cleaningFee(request.getCleaningFee())
                .serviceFee(request.getServiceFee())
                .maxGuests(request.getMaxGuests())
                .bedrooms(request.getBedrooms())
                .beds(request.getBeds())
                .bathrooms(request.getBathrooms())
                .areaSqft(request.getAreaSqft())
                .minNights(request.getMinNights())
                .maxNights(request.getMaxNights())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .houseRules(request.getHouseRules())
                .cancellationPolicy(request.getCancellationPolicy())
                .isInstantBook(request.getIsInstantBook())
                .status(PropertyStatus.PENDING)
                .host(currentUser)
                .build();
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            property.setCategory(category);
        }
        
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findByIdIn(request.getAmenityIds().stream().toList());
            property.setAmenities(new HashSet<>(amenities));
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            request.getImages().forEach(imageReq -> {
                PropertyImage image = PropertyImage.builder()
                        .imageUrl(imageReq.getImageUrl())
                        .caption(imageReq.getCaption())
                        .displayOrder(imageReq.getDisplayOrder())
                        .isPrimary(imageReq.getIsPrimary())
                        .build();
                savedProperty.addImage(image);
            });
            propertyRepository.save(savedProperty);
        }
        
        return propertyMapper.toResponse(savedProperty);
    }
    
    @Transactional
    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", id));
        
        User currentUser = userService.getCurrentUser();
        if (!property.getHost().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this property");
        }
        
        property.setIsDeleted(true);
        propertyRepository.save(property);
    }
    
    public List<String> getDistinctCities() {
        return propertyRepository.findDistinctCities();
    }
}


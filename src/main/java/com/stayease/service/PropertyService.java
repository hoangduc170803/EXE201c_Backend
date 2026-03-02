package com.stayease.service;

import com.stayease.dto.request.CreatePropertyRequest;
import com.stayease.dto.request.PropertyFilterRequest;
import com.stayease.dto.response.PageResponse;
import com.stayease.dto.response.PropertyResponse;
import com.stayease.enums.PropertyStatus;
import com.stayease.enums.RentalType;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.exception.UnauthorizedException;
import com.stayease.model.*;
import com.stayease.repository.*;
import com.stayease.utils.PropertyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityRepository amenityRepository;
    private final UserService userService;
    private final PropertyMapper propertyMapper;
    private final BookingRepository bookingRepository;

    public PropertyService(PropertyRepository propertyRepository,
            CategoryRepository categoryRepository,
            AmenityRepository amenityRepository,
            UserService userService,
            PropertyMapper propertyMapper,
            BookingRepository bookingRepository) {
        this.propertyRepository = propertyRepository;
        this.categoryRepository = categoryRepository;
        this.amenityRepository = amenityRepository;
        this.userService = userService;
        this.propertyMapper = propertyMapper;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
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

        // Validate pricing based on rental type
        RentalType rentalType = request.getRentalType() != null ? request.getRentalType() : RentalType.LONG_TERM;

        if (rentalType == RentalType.SHORT_TERM) {
            if (request.getPricePerNight() == null || request.getPricePerNight().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price per night is required and must be greater than 0 for short-term rentals");
            }
        } else if (rentalType == RentalType.LONG_TERM) {
            if (request.getPricePerMonth() == null || request.getPricePerMonth().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price per month is required and must be greater than 0 for long-term rentals");
            }
        }

        Property property = Property.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .rentalType(rentalType)
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .pricePerNight(request.getPricePerNight())
                .pricePerMonth(request.getPricePerMonth())
                .electricityCost(request.getElectricityCost())
                .waterCost(request.getWaterCost())
                .internetCost(request.getInternetCost())
                .depositMonths(request.getDepositMonths())
                .minimumLeaseMonths(request.getMinimumLeaseMonths())
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
                        .mediaType(imageReq.getMediaType() != null ? imageReq.getMediaType() : "IMAGE")
                        .fileSize(imageReq.getFileSize())
                        .duration(imageReq.getDuration())
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

    @Transactional
    public PropertyResponse updatePropertyStatus(Long id, String statusStr, Long hostId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", id));

        // Verify ownership
        if (!property.getHost().getId().equals(hostId)) {
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        // Parse and update status
        PropertyStatus newStatus;
        try {
            newStatus = PropertyStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }

        property.setStatus(newStatus);
        Property updated = propertyRepository.save(property);

        return propertyMapper.toResponse(updated);
    }

    public List<String> getDistinctCities() {
        return propertyRepository.findDistinctCities();
    }

    public List<String> getDistinctPropertyTypes() {
        return propertyRepository.findDistinctPropertyTypes();
    }

    public PageResponse<PropertyResponse> getLongTermProperties(
            Double minPrice, Double maxPrice, String city, Long categoryId,
            List<String> propertyTypes, List<Long> amenityIds, Integer minArea, Integer maxArea,
            int page, int size, String sortBy, String sortDir) {

        log.info("=== getLongTermProperties START ===");
        log.info("Params - minPrice: {}, maxPrice: {}, city: {}, categoryId: {}, propertyTypes: {}, amenityIds: {}, minArea: {}, maxArea: {}",
                minPrice, maxPrice, city, categoryId, propertyTypes, amenityIds, minArea, maxArea);
        log.info("Rental Type: LONG_TERM");

        // First, check total LONG_TERM properties in DB (without filters)
        Page<Property> allLongTermCheck = propertyRepository.filterProperties(
            null, null, null, null, null, null, null, null, null, null, null,
            RentalType.LONG_TERM,
            PageRequest.of(0, 1)
        );
        log.info("Total LONG_TERM properties in DB (no filters): {}", allLongTermCheck.getTotalElements());

        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Calculate amenitySize for query (number of amenities that must match)
        Long amenitySize = (amenityIds != null && !amenityIds.isEmpty()) ? (long) amenityIds.size() : null;

        Page<Property> propertyPage = propertyRepository.filterProperties(
            categoryId,
            minPrice,
            maxPrice,
            propertyTypes,
            city,
            null, // isInstantBook
            null, // minGuests
            minArea,
            maxArea,
            amenityIds,
            amenitySize,
            RentalType.LONG_TERM,
            pageable
        );

        log.info("After applying filters - Found {} properties out of {} total",
                propertyPage.getContent().size(), propertyPage.getTotalElements());

        if (propertyPage.getTotalElements() == 0) {
            log.warn("NO PROPERTIES FOUND! This could mean:");
            log.warn("1. No LONG_TERM properties exist in database");
            log.warn("2. All properties filtered out by: minPrice={}, maxPrice={}, city={}, categoryId={}",
                    minPrice, maxPrice, city, categoryId);
            log.warn("3. All properties have status != ACTIVE or isDeleted = true");
        }

        log.info("=== getLongTermProperties END ===");

        List<PropertyResponse> content = propertyPage.getContent().stream()
                .map(propertyMapper::toResponse)
                .collect(Collectors.toList());
        return PageResponse.from(propertyPage, content);
    }

    public PageResponse<PropertyResponse> getShortTermProperties(
            Double minPrice, Double maxPrice, String city, List<String> propertyTypes,
            List<Long> amenityIds, Integer minGuests,  LocalDate checkIn, LocalDate checkOut,
            int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Long amenitySize = (amenityIds != null && !amenityIds.isEmpty())
            ? (long) amenityIds.size()
            : null;

        Page<Property> propertyPage = propertyRepository.filterProperties(
            null, // categoryId
            minPrice,
            maxPrice,
            propertyTypes,
            city,
            null, // isInstantBook
            minGuests,
            null, // minArea (not used for SHORT_TERM)
            null, // maxArea (not used for SHORT_TERM)
            amenityIds,
            amenitySize,
            RentalType.SHORT_TERM,
            pageable
        );

        List<PropertyResponse> content;
        if (checkIn != null && checkOut != null) {
            content = propertyPage.getContent().stream()
                    .filter(property -> {
                        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                            property.getId(),
                            checkIn,
                            checkOut
                        );
                        return conflicts.isEmpty();
                    })
                    .map(propertyMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            content = propertyPage.getContent().stream()
                    .map(propertyMapper::toResponse)
                    .collect(Collectors.toList());
        }
        log.info("Service "+propertyPage +" properties");
        return PageResponse.from(propertyPage, content);
    }

    public PageResponse<PropertyResponse> filterProperties(PropertyFilterRequest filter, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Long amenitySize = (filter.getAmenityIds() != null && !filter.getAmenityIds().isEmpty())
            ? (long) filter.getAmenityIds().size()
            : null;

        // Convert single propertyType to List if present
        List<String> propertyTypes = filter.getPropertyType() != null
            ? List.of(filter.getPropertyType())
            : null;

        Page<Property> propertyPage = propertyRepository.filterProperties(
            filter.getCategoryId(),
            filter.getMinPrice(),
            filter.getMaxPrice(),
            propertyTypes,
            filter.getCity(),
            filter.getIsInstantBook(),
            filter.getMinGuests(),
            null, // minArea (not in filter DTO yet)
            null, // maxArea (not in filter DTO yet)
            filter.getAmenityIds(),
            amenitySize,
            RentalType.valueOf(filter.getRentalType()),
            pageable
        );

        // Filter out properties with conflicting bookings if checkIn and checkOut are provided
        List<PropertyResponse> content;
        if (filter.getCheckIn() != null && filter.getCheckOut() != null) {
            content = propertyPage.getContent().stream()
                    .filter(property -> {
                        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                            property.getId(),
                            filter.getCheckIn(),
                            filter.getCheckOut()
                        );
                        return conflicts.isEmpty(); // Only include properties with no conflicting bookings
                    })
                    .map(propertyMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            content = propertyPage.getContent().stream()
                    .map(propertyMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return PageResponse.from(propertyPage, content);
    }
}

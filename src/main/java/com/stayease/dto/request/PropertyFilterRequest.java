package com.stayease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFilterRequest {
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private String propertyType;  // ENTIRE_PLACE, PRIVATE_ROOM, SHARED_ROOM
    private List<Long> amenityIds;
    private String city;
    private Boolean isInstantBook;
    private Boolean freeCancellation;
    private Integer minGuests;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String rentalType;  // LONG_TERM or SHORT_TERM
}


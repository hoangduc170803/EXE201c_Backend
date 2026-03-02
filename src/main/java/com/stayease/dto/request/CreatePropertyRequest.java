package com.stayease.dto.request;

import com.stayease.enums.PropertyType;
import com.stayease.enums.RentalType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Property type is required")
    private PropertyType propertyType;
    
    // Rental type: SHORT_TERM or LONG_TERM
    private RentalType rentalType;

    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private String zipCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    // Short-term rental pricing (validated conditionally based on rentalType)
    private BigDecimal pricePerNight;
    
    // Long-term rental pricing (validated conditionally based on rentalType)
    private BigDecimal pricePerMonth;

    // Utility costs for long-term rentals
    private String electricityCost;
    private String waterCost;
    private String internetCost;

    // Deposit and lease terms
    @Min(value = 0)
    private Integer depositMonths;

    @Min(value = 1)
    private Integer minimumLeaseMonths;

    @DecimalMin(value = "0")
    private BigDecimal cleaningFee;
    
    @DecimalMin(value = "0")
    private BigDecimal serviceFee;
    
    @DecimalMin(value = "0")
    private BigDecimal securityDeposit;

    @NotNull(message = "Max guests is required")
    @Min(value = 1)
    private Integer maxGuests;
    
    @Min(value = 0)
    private Integer bedrooms;
    
    @Min(value = 0)
    private Integer beds;
    
    @Min(value = 0)
    private Integer bathrooms;
    
    private Integer areaSqft;
    
    // Short-term rental settings
    @Min(value = 1)
    private Integer minNights = 1;
    
    private Integer maxNights = 365;
    
    // Long-term rental settings
    @Min(value = 1)
    private Integer leaseDuration;

    private String checkInTime;
    private String checkOutTime;
    private String houseRules;
    private String cancellationPolicy;
    private Boolean isInstantBook = false;
    
    private Long categoryId;
    private Set<Long> amenityIds;
    private List<PropertyImageRequest> images;
}


package com.stayease.dto.response;

import com.stayease.enums.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPropertyResponse {

    private Long id;
    private String title;
    private String propertyType;
    private String rentalType;
    private String address;
    private String city;
    private BigDecimal pricePerNight;
    private BigDecimal pricePerMonth;
    private PropertyStatus status;

    // Host info
    private Long hostId;
    private String hostName;
    private String hostEmail;

    // Package subscription info
    private String currentPackage;
    private LocalDateTime subscriptionEndDate;

    // Stats
    private Long viewCount;
    private Integer totalReviews;
    private BigDecimal averageRating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


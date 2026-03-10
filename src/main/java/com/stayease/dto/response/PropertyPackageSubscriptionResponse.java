package com.stayease.dto.response;

import com.stayease.enums.PackageSubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyPackageSubscriptionResponse {

    private Long id;
    private PostingPackageResponse postingPackage;
    private Long propertyId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isActive;
    private String transactionId;
    private PackageSubscriptionStatus status;
    private LocalDateTime createdAt;
}


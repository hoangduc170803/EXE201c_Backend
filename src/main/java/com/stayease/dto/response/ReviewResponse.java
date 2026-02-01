package com.stayease.dto.response;

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
public class ReviewResponse {
    
    private Long id;
    private BigDecimal overallRating;
    private BigDecimal cleanlinessRating;
    private BigDecimal accuracyRating;
    private BigDecimal checkinRating;
    private BigDecimal communicationRating;
    private BigDecimal locationRating;
    private BigDecimal valueRating;
    private String comment;
    private String hostResponse;
    private Boolean isRecommended;
    private UserResponse user;
    private Long propertyId;
    private Long bookingId;
    private LocalDateTime createdAt;
}


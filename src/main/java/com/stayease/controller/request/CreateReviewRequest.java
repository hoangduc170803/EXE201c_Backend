package com.stayease.controller.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Overall rating is required")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal overallRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal cleanlinessRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal accuracyRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal checkinRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal communicationRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal locationRating;
    
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal valueRating;
    
    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 2000)
    private String comment;
    
    private Boolean isRecommended = true;
}


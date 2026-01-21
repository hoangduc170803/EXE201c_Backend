package com.stayease.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImageRequest {
    
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    
    private String caption;
    private Integer displayOrder;
    private Boolean isPrimary = false;
}


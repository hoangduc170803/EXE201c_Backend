package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImageResponse {
    
    private Long id;
    private String imageUrl;
    private String caption;
    private Integer displayOrder;
    private Boolean isPrimary;
}


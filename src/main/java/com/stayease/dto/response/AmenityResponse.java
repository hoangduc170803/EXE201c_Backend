package com.stayease.dto.response;

import com.stayease.enums.AmenityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponse {
    
    private Long id;
    private String name;
    private String description;
    private String icon;
    private AmenityCategory category;
}


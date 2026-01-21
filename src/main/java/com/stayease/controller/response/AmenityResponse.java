package com.stayease.controller.response;

import com.stayease.model.Amenity.AmenityCategory;
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


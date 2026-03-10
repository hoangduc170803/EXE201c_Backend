package com.stayease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentSettingRequest {

    private String settingKey;
    private String settingValue;
    private String description;
    private Boolean isActive;
    private String category;
}


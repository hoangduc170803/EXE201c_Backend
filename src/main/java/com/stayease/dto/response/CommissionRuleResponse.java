package com.stayease.dto.response;

import com.stayease.enums.CommissionAppliesTo;
import com.stayease.enums.CommissionRoundingMode;
import com.stayease.enums.CommissionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRuleResponse {

    private Long id;
    private CommissionType commissionType;
    private BigDecimal commissionPercent;
    private BigDecimal commissionFixedVnd;
    private CommissionAppliesTo commissionAppliesTo;
    private CommissionRoundingMode roundingMode;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private Boolean isActive;
    private Integer version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}


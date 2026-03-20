package com.stayease.dto.request;

import com.stayease.enums.CommissionAppliesTo;
import com.stayease.enums.CommissionRoundingMode;
import com.stayease.enums.CommissionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommissionRuleRequest {

    @NotNull
    private CommissionType commissionType;

    // Required when commissionType uses percent
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    private BigDecimal commissionPercent;

    // Required when commissionType uses fixed
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal commissionFixedVnd;

    @NotNull
    private CommissionAppliesTo commissionAppliesTo;

    @NotNull
    private CommissionRoundingMode roundingMode;

    @NotNull
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;
}


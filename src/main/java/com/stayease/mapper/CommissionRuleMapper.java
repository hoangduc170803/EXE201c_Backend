package com.stayease.mapper;

import com.stayease.dto.response.CommissionRuleResponse;
import com.stayease.model.CommissionRule;

public final class CommissionRuleMapper {

    private CommissionRuleMapper() {
    }

    public static CommissionRuleResponse toResponse(CommissionRule entity) {
        if (entity == null) return null;

        return CommissionRuleResponse.builder()
                .id(entity.getId())
                .commissionType(entity.getType())
                .commissionPercent(entity.getPercent())
                .commissionFixedVnd(entity.getFixedAmount())
                .commissionAppliesTo(entity.getAppliesTo())
                .roundingMode(entity.getRoundingMode())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .isActive(entity.getIsActive())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}


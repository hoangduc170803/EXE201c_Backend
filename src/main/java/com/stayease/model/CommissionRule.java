package com.stayease.model;

import com.stayease.enums.CommissionAppliesTo;
import com.stayease.enums.CommissionRoundingMode;
import com.stayease.enums.CommissionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRule extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private CommissionType type;

    @Column(name = "percent", precision = 5, scale = 2)
    private BigDecimal percent;

    @Column(name = "fixed_amount", precision = 18, scale = 0)
    private BigDecimal fixedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false, length = 20)
    private CommissionAppliesTo appliesTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "rounding_mode", nullable = false, length = 20)
    private CommissionRoundingMode roundingMode;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}


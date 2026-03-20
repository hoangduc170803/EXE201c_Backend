package com.stayease.model;

import com.stayease.enums.BookingSettlementStatus;
import com.stayease.enums.CommissionAppliesTo;
import com.stayease.enums.CommissionRoundingMode;
import com.stayease.enums.CommissionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "booking_settlements",
        uniqueConstraints = @UniqueConstraint(name = "uk_booking_settlement_booking", columnNames = {"booking_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSettlement extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "gross_amount", precision = 18, scale = 0, nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "commission_amount", precision = 18, scale = 0, nullable = false)
    private BigDecimal commissionAmount;

    @Column(name = "host_net_amount", precision = 18, scale = 0, nullable = false)
    private BigDecimal hostNetAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 40, nullable = false)
    private BookingSettlementStatus status;

    @Column(name = "commission_rule_id")
    private Long commissionRuleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type_snapshot", length = 30)
    private CommissionType commissionTypeSnapshot;

    @Column(name = "commission_percent_snapshot", precision = 5, scale = 2)
    private BigDecimal commissionPercentSnapshot;

    @Column(name = "commission_fixed_snapshot", precision = 18, scale = 0)
    private BigDecimal commissionFixedSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_applies_to_snapshot", length = 20)
    private CommissionAppliesTo commissionAppliesToSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_rounding_mode_snapshot", length = 20)
    private CommissionRoundingMode commissionRoundingModeSnapshot;

    @Column(name = "payment_confirmed_at")
    private LocalDateTime paymentConfirmedAt;

    @Column(name = "eligible_for_payout_at")
    private LocalDateTime eligibleForPayoutAt;

    @Column(name = "payout_id")
    private Long payoutId;
}


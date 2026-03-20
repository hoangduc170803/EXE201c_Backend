package com.stayease.service;

import com.stayease.enums.CommissionAppliesTo;
import com.stayease.enums.CommissionRoundingMode;
import com.stayease.enums.CommissionType;
import com.stayease.model.Booking;
import com.stayease.model.CommissionRule;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommissionCalculationService {

    @Value
    public static class CommissionResult {
        BigDecimal baseAmount;
        BigDecimal commissionAmount;
        BigDecimal payoutAmount;
    }

    public CommissionResult calculate(Booking booking, CommissionRule rule) {
        if (booking == null) {
            throw new IllegalArgumentException("booking is required");
        }
        if (rule == null) {
            // No rule => commission 0
            BigDecimal total = nvl(booking.getTotalPrice());
            return new CommissionResult(total, BigDecimal.ZERO, total);
        }

        BigDecimal base = resolveBaseAmount(booking, rule.getAppliesTo());
        BigDecimal commission = calculateCommissionAmount(base, rule);
        BigDecimal payout = nvl(booking.getTotalPrice()).subtract(commission);

        // never negative
        if (payout.compareTo(BigDecimal.ZERO) < 0) payout = BigDecimal.ZERO;

        return new CommissionResult(base, commission, payout);
    }

    private BigDecimal resolveBaseAmount(Booking booking, CommissionAppliesTo appliesTo) {
        if (appliesTo == CommissionAppliesTo.SUBTOTAL) {
            return nvl(booking.getSubtotal());
        }
        return nvl(booking.getTotalPrice());
    }

    private BigDecimal calculateCommissionAmount(BigDecimal base, CommissionRule rule) {
        BigDecimal commission = BigDecimal.ZERO;

        if (rule.getType() == CommissionType.PERCENT || rule.getType() == CommissionType.PERCENT_PLUS_FIXED) {
            BigDecimal percent = nvl(rule.getPercent());
            commission = commission.add(base.multiply(percent).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        }

        if (rule.getType() == CommissionType.FIXED || rule.getType() == CommissionType.PERCENT_PLUS_FIXED) {
            commission = commission.add(nvl(rule.getFixedAmount()));
        }

        // Rounding to VND
        RoundingMode rm = rule.getRoundingMode() == CommissionRoundingMode.ROUND_HALF_UP
                ? RoundingMode.HALF_UP
                : RoundingMode.DOWN;

        return commission.setScale(0, rm);
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}


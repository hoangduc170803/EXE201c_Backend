package com.stayease.service;

import com.stayease.enums.PaymentStatus;
import com.stayease.enums.RentalType;
import com.stayease.enums.SettlementRule;
import com.stayease.model.Booking;
import com.stayease.model.PaymentSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final PaymentSettingService paymentSettingService;
    private final Clock clock = Clock.systemUTC();

    public SettlementRule resolveRule(Booking booking) {
        if (booking == null || booking.getProperty() == null) {
            return SettlementRule.PAYOUT_ON_CHECKOUT;
        }
        RentalType rentalType = booking.getProperty().getRentalType();
        String key = (rentalType == RentalType.LONG_TERM)
                ? PaymentSetting.SETTLEMENT_LONG_TERM_RULE
                : PaymentSetting.SETTLEMENT_SHORT_TERM_RULE;

        String raw = paymentSettingService.getSettingValue(key);
        if (raw == null || raw.isBlank()) {
            return (rentalType == RentalType.LONG_TERM)
                    ? SettlementRule.PAYOUT_AFTER_FIRST_MONTH
                    : SettlementRule.PAYOUT_ON_CHECKOUT;
        }
        try {
            return SettlementRule.valueOf(raw.trim());
        } catch (IllegalArgumentException ex) {
            return (rentalType == RentalType.LONG_TERM)
                    ? SettlementRule.PAYOUT_AFTER_FIRST_MONTH
                    : SettlementRule.PAYOUT_ON_CHECKOUT;
        }
    }

    public LocalDateTime computeDueAt(Booking booking) {
        SettlementRule rule = resolveRule(booking);

        // Use server time; never use client timezone
        switch (rule) {
            case PAYOUT_IMMEDIATE:
                // For MVP, "immediate" means eligible as soon as payment is marked PAID.
                if (booking.getPaymentStatus() == PaymentStatus.PAID) {
                    return booking.getUpdatedAt() != null
                            ? booking.getUpdatedAt()
                            : LocalDateTime.now(clock);
                }
                return null;

            case PAYOUT_ON_CHECKIN:
                return booking.getCheckInDate() != null
                        ? booking.getCheckInDate().atStartOfDay()
                        : null;

            case PAYOUT_AFTER_FIRST_MONTH:
                return booking.getCheckInDate() != null
                        ? booking.getCheckInDate().atStartOfDay().plusDays(30)
                        : null;

            case PAYOUT_ON_CHECKOUT:
            default:
                return booking.getCheckOutDate() != null
                        ? booking.getCheckOutDate().atStartOfDay()
                        : null;
        }
    }
}


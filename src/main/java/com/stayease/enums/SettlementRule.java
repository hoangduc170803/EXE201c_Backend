package com.stayease.enums;

/**
 * Rule that determines when a booking becomes eligible for paying out to host.
 * Stored as string in PaymentSetting.
 */
public enum SettlementRule {
    PAYOUT_IMMEDIATE,
    PAYOUT_ON_CHECKIN,
    PAYOUT_ON_CHECKOUT,
    PAYOUT_AFTER_FIRST_MONTH
}


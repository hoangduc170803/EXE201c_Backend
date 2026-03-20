package com.stayease.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSetting extends BaseEntity {

    @Column(name = "setting_key", unique = true, nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "category", length = 50)
    private String category; // BANK_INFO, QR_CODE, PAYMENT_GATEWAY, etc.

    // Predefined setting keys as constants
    public static final String BANK_NAME = "BANK_NAME";
    // VietQR requires bank BIN (e.g., 970436 for Vietcombank)
    public static final String BANK_BIN = "BANK_BIN";
    public static final String BANK_ACCOUNT_NUMBER = "BANK_ACCOUNT_NUMBER";
    public static final String BANK_ACCOUNT_HOLDER = "BANK_ACCOUNT_HOLDER";
    public static final String BANK_BRANCH = "BANK_BRANCH";
    public static final String QR_CODE_URL = "QR_CODE_URL";
    public static final String MOMO_MERCHANT_ID = "MOMO_MERCHANT_ID";
    public static final String VNPAY_MERCHANT_ID = "VNPAY_MERCHANT_ID";
    public static final String PAYMENT_NOTES = "PAYMENT_NOTES";

    // Commission / Settlement configuration
    // Short-term: platform commission percent applied on total booking value
    public static final String COMMISSION_SHORT_TERM_PERCENT = "COMMISSION_SHORT_TERM_PERCENT";

    // Long-term: platform commission percent applied on first month rent (for LONG_TERM rental type)
    public static final String COMMISSION_LONG_TERM_FIRST_MONTH_PERCENT = "COMMISSION_LONG_TERM_FIRST_MONTH_PERCENT";

    // Settlement timing for short-term bookings: default PAYOUT_ON_CHECKOUT
    public static final String SETTLEMENT_SHORT_TERM_RULE = "SETTLEMENT_SHORT_TERM_RULE";

    // Settlement timing for long-term bookings: default PAYOUT_AFTER_FIRST_MONTH
    public static final String SETTLEMENT_LONG_TERM_RULE = "SETTLEMENT_LONG_TERM_RULE";
}


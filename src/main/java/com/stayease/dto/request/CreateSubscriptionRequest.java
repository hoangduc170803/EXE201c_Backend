package com.stayease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    private Long propertyId;
    private Long packageId;
    private String paymentMethod; // BANK_TRANSFER, MOMO, VNPAY, etc.
    private String transactionNote;
}


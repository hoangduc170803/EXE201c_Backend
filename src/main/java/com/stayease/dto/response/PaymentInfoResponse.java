package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoResponse {

    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String bankBranch;
    private String qrCodeUrl;
    private String paymentNotes;
}


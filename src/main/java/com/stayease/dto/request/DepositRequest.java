package com.stayease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

    private BigDecimal amount;
    private String paymentMethod; // BANK_TRANSFER, MOMO, VNPAY
    private String transactionNote;
    private String proofImageUrl; // URL của ảnh chứng từ chuyển khoản
}


package com.stayease.dto.response;

import com.stayease.enums.TransactionStatus;
import com.stayease.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponse {

    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private String description;
    private String referenceId;
    private String paymentMethod;
    private String transactionCode;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String notes;
    private LocalDateTime createdAt;
}


package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDueItemResponse {
    private Long bookingId;
    private String bookingCode;
    private String propertyTitle;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime dueAt;
    private BigDecimal totalPaidVnd;
    private BigDecimal commissionAmountVnd;
    private BigDecimal hostPayoutAmountVnd;
}


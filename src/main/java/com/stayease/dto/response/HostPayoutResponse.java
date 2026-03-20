package com.stayease.dto.response;

import com.stayease.enums.HostPayoutStatus;
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
public class HostPayoutResponse {
    private Long id;
    private Long hostId;
    private String hostName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal amountVnd;
    private HostPayoutStatus status;
    private String payoutMethod;
    private String payoutReference;
    private String adminNote;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}


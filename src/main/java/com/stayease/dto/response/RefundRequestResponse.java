package com.stayease.dto.response;

import com.stayease.enums.RefundRequestStatus;
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
public class RefundRequestResponse {
    private Long id;

    private Long bookingId;
    private String bookingCode;

    private Long guestId;
    private String guestName;

    private BigDecimal refundAmount;
    private String reason;

    private RefundRequestStatus status;

    private RefundAccountResponse refundAccount;

    private String adminNote;
    private String payoutReference;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
}


package com.stayease.dto.response;

import com.stayease.enums.BookingSettlementStatus;
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
public class BookingSettlementResponse {

    private Long id;
    private Long bookingId;
    private String bookingCode;

    private Long hostId;
    private String hostName;

    private Long guestId;
    private String guestName;

    private Long propertyId;
    private String propertyTitle;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal grossAmount;
    private BigDecimal commissionAmount;
    private BigDecimal hostNetAmount;

    private BookingSettlementStatus status;

    private Long commissionRuleId;
    private BigDecimal commissionPercentSnapshot;
    private BigDecimal commissionFixedSnapshot;

    private LocalDateTime paymentConfirmedAt;
    private LocalDateTime eligibleForPayoutAt;
    private Long payoutId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


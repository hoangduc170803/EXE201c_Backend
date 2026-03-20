package com.stayease.dto.response;

import com.stayease.enums.BookingStatus;
import com.stayease.enums.PaymentStatus;
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
public class BookingResponse {
    
    private Long id;
    private String bookingCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numGuests;
    private Integer numAdults;
    private Integer numChildren;
    private Integer numInfants;
    private BigDecimal pricePerNight;
    private Integer numNights;
    private BigDecimal subtotal;
    private BigDecimal cleaningFee;
    private BigDecimal serviceFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transferProofImageUrl;
    private String transferReference;
    private String specialRequests;
    private String guestMessage;
    private String hostResponse;

    // Cancellation / Refund
    private String cancellationReason;
    private String cancelledBy;
    private BigDecimal refundAmount;

    // Commission / Payout snapshot (captured when payment is confirmed)
    private BigDecimal commissionAmountVnd;
    private BigDecimal hostPayoutAmountVnd;

    private UserResponse guest;
    private PropertyResponse property;
    private LocalDateTime createdAt;
}


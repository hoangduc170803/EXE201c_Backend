package com.stayease.service;

import com.stayease.dto.response.BookingSettlementResponse;
import com.stayease.enums.BookingSettlementStatus;
import com.stayease.enums.PaymentStatus;
import com.stayease.exception.BadRequestException;
import com.stayease.model.Booking;
import com.stayease.model.BookingSettlement;
import com.stayease.model.CommissionRule;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.BookingSettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingSettlementService {

    private final BookingRepository bookingRepository;
    private final BookingSettlementRepository bookingSettlementRepository;
    private final CommissionRuleService commissionRuleService;
    private final CommissionCalculationService commissionCalculationService;
    private final SettlementService settlementService;

    @Transactional
    public BookingSettlement createOrRecalculateForBooking(Long bookingId, LocalDateTime confirmationTime) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("Booking must be PAID before settlement is calculated");
        }

        LocalDateTime confirmedAt = confirmationTime != null ? confirmationTime : LocalDateTime.now();
        CommissionRule activeRule = commissionRuleService.getActiveRule(confirmedAt);
        CommissionCalculationService.CommissionResult calc = commissionCalculationService.calculate(booking, activeRule);

        booking.setCommissionRuleId(activeRule != null ? activeRule.getId() : null);
        booking.setCommissionType(activeRule != null ? activeRule.getType() : null);
        booking.setCommissionPercent(activeRule != null ? activeRule.getPercent() : null);
        booking.setCommissionFixedVnd(activeRule != null ? activeRule.getFixedAmount() : null);
        booking.setCommissionAppliesTo(activeRule != null ? activeRule.getAppliesTo() : null);
        booking.setCommissionRoundingMode(activeRule != null ? activeRule.getRoundingMode() : null);
        booking.setCommissionAmountVnd(calc.getCommissionAmount());
        booking.setHostPayoutAmountVnd(calc.getPayoutAmount());
        bookingRepository.save(booking);

        BigDecimal gross = nvl(booking.getTotalPrice()).setScale(0, RoundingMode.HALF_UP);
        BigDecimal commission = nvl(calc.getCommissionAmount()).setScale(0, RoundingMode.HALF_UP);
        BigDecimal net = gross.subtract(commission);
        if (net.compareTo(BigDecimal.ZERO) < 0) {
            net = BigDecimal.ZERO;
        }

        LocalDateTime eligibleAt = settlementService.computeDueAt(booking);
        BookingSettlement settlement = bookingSettlementRepository.findByBookingId(bookingId)
                .orElse(BookingSettlement.builder().booking(booking).build());

        settlement.setGrossAmount(gross);
        settlement.setCommissionAmount(commission);
        settlement.setHostNetAmount(net);
        settlement.setCommissionRuleId(activeRule != null ? activeRule.getId() : null);
        settlement.setCommissionTypeSnapshot(activeRule != null ? activeRule.getType() : null);
        settlement.setCommissionPercentSnapshot(activeRule != null ? activeRule.getPercent() : null);
        settlement.setCommissionFixedSnapshot(activeRule != null ? activeRule.getFixedAmount() : null);
        settlement.setCommissionAppliesToSnapshot(activeRule != null ? activeRule.getAppliesTo() : null);
        settlement.setCommissionRoundingModeSnapshot(activeRule != null ? activeRule.getRoundingMode() : null);
        settlement.setPaymentConfirmedAt(confirmedAt);
        settlement.setEligibleForPayoutAt(eligibleAt);

        if (settlement.getStatus() == null
                || settlement.getStatus() == BookingSettlementStatus.WAITING_PAYMENT_CONFIRMATION
                || settlement.getStatus() == BookingSettlementStatus.CALCULATED_WAITING_RELEASE
                || settlement.getStatus() == BookingSettlementStatus.READY_FOR_PAYOUT) {
            settlement.setStatus(resolveCalculatedStatus(eligibleAt, confirmedAt));
        }

        return bookingSettlementRepository.save(settlement);
    }

    @Transactional
    public BookingSettlement markReady(Long settlementId) {
        BookingSettlement settlement = bookingSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found"));

        if (settlement.getStatus() == BookingSettlementStatus.PAID_OUT) {
            throw new BadRequestException("Paid out settlement cannot be moved to READY");
        }

        settlement.setStatus(BookingSettlementStatus.READY_FOR_PAYOUT);
        return bookingSettlementRepository.save(settlement);
    }

    @Transactional
    public BookingSettlement markPaidOut(Long settlementId, Long payoutId) {
        BookingSettlement settlement = bookingSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found"));

        settlement.setStatus(BookingSettlementStatus.PAID_OUT);
        settlement.setPayoutId(payoutId);
        return bookingSettlementRepository.save(settlement);
    }

    @Transactional(readOnly = true)
    public Page<BookingSettlementResponse> listForAdmin(BookingSettlementStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingSettlement> result = (status == null)
                ? bookingSettlementRepository.findAll(pageable)
                : bookingSettlementRepository.findByStatus(status, pageable);
        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public BookingSettlementResponse getById(Long settlementId) {
        BookingSettlement settlement = bookingSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("Settlement not found"));
        return toResponse(settlement);
    }

    @Transactional(readOnly = true)
    public Page<BookingSettlementResponse> listForHost(Long hostId, BookingSettlementStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingSettlement> result = (status == null)
                ? bookingSettlementRepository.findByHostId(hostId, pageable)
                : bookingSettlementRepository.findByHostIdAndStatus(hostId, status, pageable);
        return result.map(this::toResponse);
    }

    private BookingSettlementStatus resolveCalculatedStatus(LocalDateTime eligibleAt, LocalDateTime now) {
        if (eligibleAt != null && (eligibleAt.isBefore(now) || eligibleAt.isEqual(now))) {
            return BookingSettlementStatus.READY_FOR_PAYOUT;
        }
        return BookingSettlementStatus.CALCULATED_WAITING_RELEASE;
    }

    private BookingSettlementResponse toResponse(BookingSettlement settlement) {
        Booking booking = settlement.getBooking();
        String hostName = null;
        if (booking.getProperty() != null && booking.getProperty().getHost() != null) {
            String first = booking.getProperty().getHost().getFirstName() == null ? "" : booking.getProperty().getHost().getFirstName();
            String last = booking.getProperty().getHost().getLastName() == null ? "" : booking.getProperty().getHost().getLastName();
            hostName = (first + " " + last).trim();
        }

        String guestName = null;
        if (booking.getGuest() != null) {
            String first = booking.getGuest().getFirstName() == null ? "" : booking.getGuest().getFirstName();
            String last = booking.getGuest().getLastName() == null ? "" : booking.getGuest().getLastName();
            guestName = (first + " " + last).trim();
        }

        return BookingSettlementResponse.builder()
                .id(settlement.getId())
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .hostId(booking.getProperty() != null && booking.getProperty().getHost() != null ? booking.getProperty().getHost().getId() : null)
                .hostName(hostName)
                .guestId(booking.getGuest() != null ? booking.getGuest().getId() : null)
                .guestName(guestName)
                .propertyId(booking.getProperty() != null ? booking.getProperty().getId() : null)
                .propertyTitle(booking.getProperty() != null ? booking.getProperty().getTitle() : null)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .grossAmount(settlement.getGrossAmount())
                .commissionAmount(settlement.getCommissionAmount())
                .hostNetAmount(settlement.getHostNetAmount())
                .status(settlement.getStatus())
                .commissionRuleId(settlement.getCommissionRuleId())
                .commissionPercentSnapshot(settlement.getCommissionPercentSnapshot())
                .commissionFixedSnapshot(settlement.getCommissionFixedSnapshot())
                .paymentConfirmedAt(settlement.getPaymentConfirmedAt())
                .eligibleForPayoutAt(settlement.getEligibleForPayoutAt())
                .payoutId(settlement.getPayoutId())
                .createdAt(settlement.getCreatedAt())
                .updatedAt(settlement.getUpdatedAt())
                .build();
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}



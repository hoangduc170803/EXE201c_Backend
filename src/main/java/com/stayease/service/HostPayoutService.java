package com.stayease.service;

import com.stayease.dto.response.HostPayoutDetailResponse;
import com.stayease.dto.response.HostPayoutResponse;
import com.stayease.dto.response.SettlementDueItemResponse;
import com.stayease.enums.BookingSettlementStatus;
import com.stayease.enums.BookingStatus;
import com.stayease.enums.HostPayoutStatus;
import com.stayease.enums.PaymentStatus;
import com.stayease.model.Booking;
import com.stayease.model.BookingSettlement;
import com.stayease.model.HostPayout;
import com.stayease.model.HostPayoutItem;
import com.stayease.model.User;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.BookingSettlementRepository;
import com.stayease.repository.HostPayoutItemRepository;
import com.stayease.repository.HostPayoutRepository;
import com.stayease.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostPayoutService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HostPayoutRepository hostPayoutRepository;
    private final HostPayoutItemRepository hostPayoutItemRepository;
    private final SettlementService settlementService;
    private final BookingSettlementRepository bookingSettlementRepository;

    private final Clock clock = Clock.systemUTC();

    /**
     * Computed view: bookings that are eligible for payout to the host.
     */
    @Transactional(readOnly = true)
    public List<SettlementDueItemResponse> listDueItemsForHost(Long hostId) {
        // Use server-side host query to avoid scanning the whole table.
        // Using a large page size for MVP (can be paginated later).
        List<Booking> bookings = bookingRepository.findByHostId(hostId, PageRequest.of(0, 10_000)).getContent();

        LocalDateTime now = LocalDateTime.now(clock);
        List<SettlementDueItemResponse> result = new ArrayList<>();

        for (Booking b : bookings) {
            if (b.getProperty() == null || b.getProperty().getHost() == null) continue;
            if (b.getPaymentStatus() != PaymentStatus.PAID) continue;
            if (b.getStatus() == BookingStatus.CANCELLED) continue;
            if (b.getHostPayoutAmountVnd() == null) continue;

            // Exclude already paid/assigned booking
            Optional<HostPayoutItem> existingItem = hostPayoutItemRepository.findByBookingId(b.getId());
            if (existingItem.isPresent() && existingItem.get().getStatus() != HostPayoutStatus.CANCELLED) {
                continue;
            }

            LocalDateTime dueAt = settlementService.computeDueAt(b);
            if (dueAt == null) continue;
            if (now.isBefore(dueAt)) continue;

            result.add(SettlementDueItemResponse.builder()
                    .bookingId(b.getId())
                    .bookingCode(b.getBookingCode())
                    .propertyTitle(b.getProperty().getTitle())
                    .checkInDate(b.getCheckInDate())
                    .checkOutDate(b.getCheckOutDate())
                    .dueAt(dueAt)
                    .totalPaidVnd(b.getTotalPrice())
                    .commissionAmountVnd(b.getCommissionAmountVnd())
                    .hostPayoutAmountVnd(b.getHostPayoutAmountVnd())
                    .build());
        }

        // Sort newest due first (or earliest due first). We'll choose earliest due first.
        result.sort(Comparator.comparing(SettlementDueItemResponse::getDueAt));
        return result;
    }

    @Transactional(readOnly = true)
    public Page<HostPayoutResponse> hostListMyPayouts(Long hostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HostPayout> payouts = hostPayoutRepository.findByHostId(hostId, pageable);
        return payouts.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public HostPayoutDetailResponse hostGetPayoutDetail(Long hostId, Long payoutId) {
        HostPayout payout = hostPayoutRepository.findById(payoutId)
                .orElseThrow(() -> new EntityNotFoundException("Payout not found"));
        if (!Objects.equals(payout.getHost().getId(), hostId)) {
            throw new SecurityException("Not allowed");
        }
        return getPayoutDetailInternal(payout);
    }

    @Transactional(readOnly = true)
    public Page<HostPayoutResponse> adminListPayouts(HostPayoutStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HostPayout> payouts = (status == null)
                ? hostPayoutRepository.findAll(pageable)
                : hostPayoutRepository.findByStatus(status, pageable);
        return payouts.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public HostPayoutDetailResponse adminGetPayoutDetail(Long payoutId) {
        HostPayout payout = hostPayoutRepository.findById(payoutId)
                .orElseThrow(() -> new EntityNotFoundException("Payout not found"));
        return getPayoutDetailInternal(payout);
    }

    @Transactional(readOnly = true)
    public List<SettlementDueItemResponse> adminListDueItemsForHost(Long hostId) {
        // Same as host view; method kept for clearer admin API.
        return listDueItemsForHost(hostId);
    }

    @Transactional
    public HostPayoutDetailResponse adminCreatePayout(Long hostId, List<Long> bookingIds, String adminNote) {
        if (bookingIds == null || bookingIds.isEmpty()) {
            throw new IllegalArgumentException("bookingIds is required");
        }

        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new EntityNotFoundException("Host not found"));

        // Load bookings and validate eligibility
        List<Booking> bookings = bookingRepository.findAllById(bookingIds);
        Map<Long, Booking> bookingMap = bookings.stream().collect(Collectors.toMap(Booking::getId, Function.identity()));
        for (Long id : bookingIds) {
            if (!bookingMap.containsKey(id)) {
                throw new EntityNotFoundException("Booking not found: " + id);
            }
        }

        List<BookingSettlement> settlements = bookingSettlementRepository.findByBookingIdIn(bookingIds);
        Map<Long, BookingSettlement> settlementMap = settlements.stream()
                .collect(Collectors.toMap(s -> s.getBooking().getId(), Function.identity()));

        LocalDateTime now = LocalDateTime.now(clock);
        List<HostPayoutItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Booking b : bookings) {
            if (b.getProperty() == null || b.getProperty().getHost() == null || !Objects.equals(b.getProperty().getHost().getId(), hostId)) {
                throw new IllegalArgumentException("Booking does not belong to host: " + b.getId());
            }
            if (b.getPaymentStatus() != PaymentStatus.PAID) {
                throw new IllegalArgumentException("Booking is not paid: " + b.getId());
            }
            if (b.getStatus() == BookingStatus.CANCELLED) {
                throw new IllegalArgumentException("Booking cancelled: " + b.getId());
            }
            if (b.getHostPayoutAmountVnd() == null) {
                throw new IllegalArgumentException("Booking missing host payout amount: " + b.getId());
            }
            BookingSettlement settlement = settlementMap.get(b.getId());
            if (settlement == null) {
                throw new IllegalArgumentException("Booking missing settlement: " + b.getId());
            }
            if (settlement.getStatus() != BookingSettlementStatus.READY_FOR_PAYOUT) {
                throw new IllegalArgumentException("Settlement not READY_FOR_PAYOUT: " + b.getId());
            }

            LocalDateTime dueAt = settlement.getEligibleForPayoutAt() != null ? settlement.getEligibleForPayoutAt() : settlementService.computeDueAt(b);
            if (dueAt != null && now.isBefore(dueAt)) {
                throw new IllegalArgumentException("Booking not yet due: " + b.getId());
            }

            Optional<HostPayoutItem> existing = hostPayoutItemRepository.findByBookingId(b.getId());
            if (existing.isPresent() && existing.get().getStatus() != HostPayoutStatus.CANCELLED) {
                throw new IllegalArgumentException("Booking already has payout item: " + b.getId());
            }

            total = total.add(b.getHostPayoutAmountVnd());
            items.add(HostPayoutItem.builder()
                    .booking(b)
                    .dueAt(dueAt)
                    .amountVnd(b.getHostPayoutAmountVnd())
                    .status(HostPayoutStatus.PROCESSING)
                    .build());
        }

        HostPayout payout = HostPayout.builder()
                .host(host)
                .amountVnd(total)
                .status(HostPayoutStatus.PROCESSING)
                .payoutMethod("BANK_TRANSFER")
                .adminNote(adminNote)
                .approvedAt(now)
                .build();
        payout = hostPayoutRepository.save(payout);

        for (HostPayoutItem item : items) {
            item.setPayout(payout);
        }
        hostPayoutItemRepository.saveAll(items);

        // Mark settlements as in payout and link payoutId
        for (BookingSettlement s : settlements) {
            if (bookingIds.contains(s.getBooking().getId())) {
                s.setStatus(BookingSettlementStatus.IN_PAYOUT);
                s.setPayoutId(payout.getId());
            }
        }
        bookingSettlementRepository.saveAll(settlements);

        return getPayoutDetailInternal(payout);
    }

    @Transactional
    public HostPayoutDetailResponse adminCreatePayoutFromReady(Long hostId, String adminNote) {
        List<BookingSettlement> readySettlements = bookingSettlementRepository
                .findByBookingPropertyHostIdAndStatus(hostId, BookingSettlementStatus.READY_FOR_PAYOUT);
        if (readySettlements.isEmpty()) {
            throw new IllegalArgumentException("Không có settlement READY_FOR_PAYOUT cho host");
        }
        List<Long> bookingIds = readySettlements.stream()
                .map(bs -> bs.getBooking().getId())
                .toList();
        return adminCreatePayout(hostId, bookingIds, adminNote);
    }

    @Transactional
    public HostPayoutResponse adminMarkPaid(Long payoutId, String payoutReference, String adminNote) {
        HostPayout payout = hostPayoutRepository.findById(payoutId)
                .orElseThrow(() -> new EntityNotFoundException("Payout not found"));

        if (payout.getStatus() == HostPayoutStatus.CANCELLED) {
            throw new IllegalStateException("Payout already cancelled");
        }

        payout.setStatus(HostPayoutStatus.PAID);
        payout.setPayoutReference(payoutReference);
        if (adminNote != null && !adminNote.isBlank()) {
            payout.setAdminNote(adminNote);
        }
        payout.setPaidAt(LocalDateTime.now(clock));
        hostPayoutRepository.save(payout);

        List<HostPayoutItem> items = hostPayoutItemRepository.findByPayoutId(payoutId);
        for (HostPayoutItem i : items) {
            i.setStatus(HostPayoutStatus.PAID);
        }
        hostPayoutItemRepository.saveAll(items);

        List<BookingSettlement> settlements = bookingSettlementRepository.findByPayoutId(payoutId);
        for (BookingSettlement s : settlements) {
            s.setStatus(BookingSettlementStatus.PAID_OUT);
            s.setPayoutId(payoutId);
        }
        bookingSettlementRepository.saveAll(settlements);

        return toResponse(payout);
    }

    @Transactional
    public HostPayoutResponse adminCancel(Long payoutId, String adminNote) {
        HostPayout payout = hostPayoutRepository.findById(payoutId)
                .orElseThrow(() -> new EntityNotFoundException("Payout not found"));

        if (payout.getStatus() == HostPayoutStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid payout");
        }

        payout.setStatus(HostPayoutStatus.CANCELLED);
        if (adminNote != null && !adminNote.isBlank()) {
            payout.setAdminNote(adminNote);
        }
        hostPayoutRepository.save(payout);

        List<HostPayoutItem> items = hostPayoutItemRepository.findByPayoutId(payoutId);
        for (HostPayoutItem i : items) {
            i.setStatus(HostPayoutStatus.CANCELLED);
            i.setPayout(null);
        }
        hostPayoutItemRepository.saveAll(items);

        List<BookingSettlement> settlements = bookingSettlementRepository.findByPayoutId(payoutId);
        for (BookingSettlement s : settlements) {
            s.setStatus(BookingSettlementStatus.READY_FOR_PAYOUT);
            s.setPayoutId(null);
        }
        bookingSettlementRepository.saveAll(settlements);

        return toResponse(payout);
    }

    private HostPayoutDetailResponse getPayoutDetailInternal(HostPayout payout) {
        List<HostPayoutItem> items = hostPayoutItemRepository.findByPayoutId(payout.getId());
        List<SettlementDueItemResponse> itemResponses = items.stream().map(i -> {
            Booking b = i.getBooking();
            return SettlementDueItemResponse.builder()
                    .bookingId(b.getId())
                    .bookingCode(b.getBookingCode())
                    .propertyTitle(b.getProperty() != null ? b.getProperty().getTitle() : null)
                    .checkInDate(b.getCheckInDate())
                    .checkOutDate(b.getCheckOutDate())
                    .dueAt(i.getDueAt())
                    .totalPaidVnd(b.getTotalPrice())
                    .commissionAmountVnd(b.getCommissionAmountVnd())
                    .hostPayoutAmountVnd(i.getAmountVnd())
                    .build();
        }).collect(Collectors.toList());

        return HostPayoutDetailResponse.builder()
                .payout(toResponse(payout))
                .items(itemResponses)
                .build();
    }

    private HostPayoutResponse toResponse(HostPayout payout) {
        String hostName = null;
        if (payout.getHost() != null) {
            hostName = (payout.getHost().getFirstName() == null ? "" : payout.getHost().getFirstName())
                    + " " + (payout.getHost().getLastName() == null ? "" : payout.getHost().getLastName());
            hostName = hostName.trim();
        }
        return HostPayoutResponse.builder()
                .id(payout.getId())
                .hostId(payout.getHost() != null ? payout.getHost().getId() : null)
                .hostName(hostName)
                .periodStart(payout.getPeriodStart())
                .periodEnd(payout.getPeriodEnd())
                .amountVnd(payout.getAmountVnd())
                .status(payout.getStatus())
                .payoutMethod(payout.getPayoutMethod())
                .payoutReference(payout.getPayoutReference())
                .adminNote(payout.getAdminNote())
                .approvedAt(payout.getApprovedAt())
                .paidAt(payout.getPaidAt())
                .createdAt(payout.getCreatedAt())
                .build();
    }
}


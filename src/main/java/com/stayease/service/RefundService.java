package com.stayease.service;

import com.stayease.dto.request.CreateRefundAccountRequest;
import com.stayease.dto.request.CreateRefundRequestRequest;
import com.stayease.dto.request.UpdateRefundRequestAdminRequest;
import com.stayease.dto.response.RefundAccountResponse;
import com.stayease.dto.response.RefundRequestResponse;
import com.stayease.enums.BookingStatus;
import com.stayease.enums.PaymentStatus;
import com.stayease.enums.RefundRequestStatus;
import com.stayease.exception.BadRequestException;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.exception.UnauthorizedException;
import com.stayease.model.Booking;
import com.stayease.model.RefundAccount;
import com.stayease.model.RefundRequest;
import com.stayease.model.User;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.RefundAccountRepository;
import com.stayease.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundAccountRepository refundAccountRepository;
    private final RefundRequestRepository refundRequestRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Transactional
    public RefundAccountResponse createRefundAccount(CreateRefundAccountRequest request) {
        User current = userService.getCurrentUser();

        RefundAccount account = RefundAccount.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .user(current)
                .build();

        RefundAccount saved = refundAccountRepository.save(account);

        if (Boolean.TRUE.equals(saved.getIsDefault())) {
            makeOnlyDefault(current.getId(), saved.getId());
        }

        return toAccountResponse(saved);
    }

    public List<RefundAccountResponse> getMyRefundAccounts() {
        User current = userService.getCurrentUser();
        return refundAccountRepository.findByUserIdOrderByCreatedAtDesc(current.getId())
                .stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RefundAccountResponse setDefaultRefundAccount(Long refundAccountId) {
        User current = userService.getCurrentUser();
        RefundAccount account = refundAccountRepository.findById(refundAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("RefundAccount", "id", refundAccountId));

        if (!account.getUser().getId().equals(current.getId())) {
            throw new UnauthorizedException("You are not allowed to modify this refund account");
        }

        account.setIsDefault(true);
        RefundAccount saved = refundAccountRepository.save(account);
        makeOnlyDefault(current.getId(), saved.getId());
        return toAccountResponse(saved);
    }

    @Transactional
    public void deleteRefundAccount(Long refundAccountId) {
        User current = userService.getCurrentUser();
        RefundAccount account = refundAccountRepository.findById(refundAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("RefundAccount", "id", refundAccountId));

        if (!account.getUser().getId().equals(current.getId())) {
            throw new UnauthorizedException("You are not allowed to delete this refund account");
        }

        refundAccountRepository.delete(account);
    }

    @Transactional
    public RefundRequestResponse createRefundRequest(Long bookingId, CreateRefundRequestRequest request) {
        User current = userService.getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (!booking.getGuest().getId().equals(current.getId())) {
            throw new UnauthorizedException("Only the guest can request a refund for this booking");
        }

        // Must be cancelled and refundable
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new BadRequestException("Refund request is only allowed for cancelled bookings");
        }

        BigDecimal refundAmount = booking.getRefundAmount() == null ? BigDecimal.ZERO : booking.getRefundAmount();
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("This booking does not have a refundable amount");
        }

        // Must have been paid (platform received money)
        if (booking.getPaymentStatus() != PaymentStatus.PAID && booking.getPaymentStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new BadRequestException("Refund request requires the booking to be paid");
        }

        if (refundRequestRepository.findByBookingId(bookingId).isPresent()) {
            throw new BadRequestException("Refund request already exists for this booking");
        }

        RefundAccount account = refundAccountRepository.findById(request.getRefundAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("RefundAccount", "id", request.getRefundAccountId()));

        if (!account.getUser().getId().equals(current.getId())) {
            throw new UnauthorizedException("Refund account does not belong to the current user");
        }

        RefundRequest refundRequest = RefundRequest.builder()
                .booking(booking)
                .guest(current)
                .refundAccount(account)
                .refundAmount(refundAmount)
                .reason(request.getReason())
                .status(RefundRequestStatus.REQUESTED)
                .build();

        RefundRequest saved = refundRequestRepository.save(refundRequest);
        return toRefundResponse(saved);
    }

    public Page<RefundRequestResponse> getMyRefundRequests(int page, int size) {
        User current = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return refundRequestRepository.findByGuestIdOrderByCreatedAtDesc(current.getId(), pageable)
                .map(this::toRefundResponse);
    }

    /**
     * Return the refund request for a given booking if it exists.
     * Authorization: booking owner (guest) or ADMIN.
     */
    public RefundRequestResponse getRefundRequestForBooking(Long bookingId) {
        User current = userService.getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        boolean isGuest = booking.getGuest() != null && booking.getGuest().getId().equals(current.getId());
        boolean isAdmin = current.getRoles() != null && current.getRoles().stream()
                .anyMatch(r -> r.getName() != null && r.getName().name().equals("ROLE_ADMIN"));

        if (!isGuest && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to view refund request for this booking");
        }

        return refundRequestRepository.findByBookingId(bookingId)
                .map(this::toRefundResponse)
                .orElse(null);
    }

    public Page<RefundRequestResponse> adminListRefundRequests(RefundRequestStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RefundRequest> result = (status == null)
                ? refundRequestRepository.findAllByOrderByCreatedAtDesc(pageable)
                : refundRequestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);

        return result.map(this::toRefundResponse);
    }

    @Transactional
    public RefundRequestResponse adminUpdateRefundRequest(Long refundRequestId, UpdateRefundRequestAdminRequest request) {
        User admin = userService.getCurrentUser();

        RefundRequest rr = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RefundRequest", "id", refundRequestId));

        RefundRequestStatus target = request.getStatus();
        if (target == RefundRequestStatus.APPROVED) {
            if (rr.getStatus() != RefundRequestStatus.REQUESTED) {
                throw new BadRequestException("Only REQUESTED refund requests can be approved");
            }
            rr.setStatus(RefundRequestStatus.APPROVED);
            rr.setApprovedAt(LocalDateTime.now());
            rr.setApprovedByAdmin(admin);
            rr.setAdminNote(request.getAdminNote());
        } else if (target == RefundRequestStatus.REJECTED) {
            if (rr.getStatus() != RefundRequestStatus.REQUESTED) {
                throw new BadRequestException("Only REQUESTED refund requests can be rejected");
            }
            rr.setStatus(RefundRequestStatus.REJECTED);
            rr.setAdminNote(request.getAdminNote());
        } else if (target == RefundRequestStatus.PAID) {
            if (rr.getStatus() != RefundRequestStatus.APPROVED) {
                throw new BadRequestException("Only APPROVED refund requests can be marked as PAID");
            }
            rr.setStatus(RefundRequestStatus.PAID);
            rr.setPaidAt(LocalDateTime.now());
            rr.setPaidByAdmin(admin);
            rr.setPayoutReference(request.getPayoutReference());
            rr.setAdminNote(request.getAdminNote());

            // Update booking payment status
            Booking booking = rr.getBooking();
            BigDecimal total = booking.getTotalPrice() == null ? BigDecimal.ZERO : booking.getTotalPrice();
            BigDecimal refund = rr.getRefundAmount() == null ? BigDecimal.ZERO : rr.getRefundAmount();

            if (refund.compareTo(total) >= 0) {
                booking.setPaymentStatus(PaymentStatus.REFUNDED);
            } else {
                booking.setPaymentStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }

            bookingRepository.save(booking);
        } else {
            throw new BadRequestException("Unsupported status transition");
        }

        refundRequestRepository.save(rr);
        return toRefundResponse(rr);
    }

    private void makeOnlyDefault(Long userId, Long defaultId) {
        // flip others
        List<RefundAccount> accounts = refundAccountRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (RefundAccount a : accounts) {
            boolean shouldDefault = a.getId().equals(defaultId);
            if (a.getIsDefault() == null || a.getIsDefault() != shouldDefault) {
                a.setIsDefault(shouldDefault);
                refundAccountRepository.save(a);
            }
        }
    }

    private RefundAccountResponse toAccountResponse(RefundAccount a) {
        return RefundAccountResponse.builder()
                .id(a.getId())
                .bankName(a.getBankName())
                .accountNumber(a.getAccountNumber())
                .accountHolder(a.getAccountHolder())
                .isDefault(a.getIsDefault())
                .build();
    }

    private RefundRequestResponse toRefundResponse(RefundRequest rr) {
        Booking b = rr.getBooking();
        String guestName = rr.getGuest() != null ? rr.getGuest().getFullName() : null;
        return RefundRequestResponse.builder()
                .id(rr.getId())
                .bookingId(b != null ? b.getId() : null)
                .bookingCode(b != null ? b.getBookingCode() : null)
                .guestId(rr.getGuest() != null ? rr.getGuest().getId() : null)
                .guestName(guestName)
                .refundAmount(rr.getRefundAmount())
                .reason(rr.getReason())
                .status(rr.getStatus())
                .refundAccount(rr.getRefundAccount() != null ? toAccountResponse(rr.getRefundAccount()) : null)
                .adminNote(rr.getAdminNote())
                .payoutReference(rr.getPayoutReference())
                .createdAt(rr.getCreatedAt())
                .approvedAt(rr.getApprovedAt())
                .paidAt(rr.getPaidAt())
                .build();
    }
}


package com.stayease.controller;

import com.stayease.dto.response.BookingSettlementResponse;
import com.stayease.enums.BookingSettlementStatus;
import com.stayease.model.BookingSettlement;
import com.stayease.model.User;
import com.stayease.repository.UserRepository;
import com.stayease.service.BookingSettlementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingSettlementController {

    private final BookingSettlementService bookingSettlementService;
    private final UserRepository userRepository;

    @GetMapping("/admin/settlements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingSettlementResponse>> adminListSettlements(
            @RequestParam(required = false) BookingSettlementStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(bookingSettlementService.listForAdmin(status, page, size));
    }

    @PatchMapping("/admin/settlements/{id}/mark-ready")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingSettlementResponse> adminMarkReady(@PathVariable Long id) {
        BookingSettlement settlement = bookingSettlementService.markReady(id);
        return ResponseEntity.ok(bookingSettlementService.getById(settlement.getId()));
    }

    @PatchMapping("/admin/settlements/{id}/mark-paid-out")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingSettlementResponse> adminMarkPaidOut(
            @PathVariable Long id,
            @RequestParam(required = false) Long payoutId
    ) {
        BookingSettlement settlement = bookingSettlementService.markPaidOut(id, payoutId);
        return ResponseEntity.ok(bookingSettlementService.getById(settlement.getId()));
    }

    @GetMapping("/host/settlements")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<Page<BookingSettlementResponse>> hostListSettlements(
            @RequestParam(required = false) BookingSettlementStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long hostId = resolveUserId(authentication);
        return ResponseEntity.ok(bookingSettlementService.listForHost(hostId, status, page, size));
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new EntityNotFoundException("User not authenticated");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }

}



package com.stayease.controller;

import com.stayease.dto.request.AutoCreateHostPayoutRequest;
import com.stayease.dto.request.CreateHostPayoutRequest;
import com.stayease.dto.request.MarkHostPayoutPaidRequest;
import com.stayease.dto.response.HostPayoutDetailResponse;
import com.stayease.dto.response.HostPayoutResponse;
import com.stayease.dto.response.SettlementDueItemResponse;
import com.stayease.enums.HostPayoutStatus;
import com.stayease.model.User;
import com.stayease.repository.UserRepository;
import com.stayease.service.HostPayoutService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayoutController {

    private final HostPayoutService hostPayoutService;
    private final UserRepository userRepository;

    // ---------------------- HOST ----------------------

    @GetMapping("/host/settlements/due-items")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<List<SettlementDueItemResponse>> hostListDueItems(Authentication authentication) {
        Long hostId = resolveUserId(authentication);
        return ResponseEntity.ok(hostPayoutService.listDueItemsForHost(hostId));
    }

    @GetMapping("/host/payouts")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<Page<HostPayoutResponse>> hostListPayouts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long hostId = resolveUserId(authentication);
        return ResponseEntity.ok(hostPayoutService.hostListMyPayouts(hostId, page, size));
    }

    @GetMapping("/host/payouts/{id}")
    @PreAuthorize("hasRole('HOST')")
    public ResponseEntity<HostPayoutDetailResponse> hostGetPayout(@PathVariable Long id, Authentication authentication) {
        Long hostId = resolveUserId(authentication);
        return ResponseEntity.ok(hostPayoutService.hostGetPayoutDetail(hostId, id));
    }

    // ---------------------- ADMIN ----------------------

    @GetMapping("/admin/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<HostPayoutResponse>> adminListPayouts(
            @RequestParam(required = false) HostPayoutStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(hostPayoutService.adminListPayouts(status, page, size));
    }

    @GetMapping("/admin/payouts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HostPayoutDetailResponse> adminGetPayout(@PathVariable Long id) {
        return ResponseEntity.ok(hostPayoutService.adminGetPayoutDetail(id));
    }

    @GetMapping("/admin/payouts/due-items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SettlementDueItemResponse>> adminListDueItems(@RequestParam Long hostId) {
        return ResponseEntity.ok(hostPayoutService.adminListDueItemsForHost(hostId));
    }

    @PostMapping("/admin/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HostPayoutDetailResponse> adminCreatePayout(@Valid @RequestBody CreateHostPayoutRequest request) {
        return ResponseEntity.ok(hostPayoutService.adminCreatePayout(request.getHostId(), request.getBookingIds(), request.getAdminNote()));
    }

    @PostMapping("/admin/payouts/auto-create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HostPayoutDetailResponse> adminAutoCreatePayout(
            @Valid @RequestBody AutoCreateHostPayoutRequest request
    ) {
        return ResponseEntity.ok(hostPayoutService.adminCreatePayoutFromReady(request.getHostId(), request.getAdminNote()));
    }

    @PutMapping("/admin/payouts/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HostPayoutResponse> adminMarkPaid(
            @PathVariable Long id,
            @Valid @RequestBody MarkHostPayoutPaidRequest request
    ) {
        return ResponseEntity.ok(hostPayoutService.adminMarkPaid(id, request.getPayoutReference(), request.getAdminNote()));
    }

    @PutMapping("/admin/payouts/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HostPayoutResponse> adminCancel(
            @PathVariable Long id,
            @RequestBody(required = false) String adminNote
    ) {
        return ResponseEntity.ok(hostPayoutService.adminCancel(id, adminNote));
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

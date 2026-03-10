package com.stayease.controller;

import com.stayease.dto.request.DepositRequest;
import com.stayease.dto.response.PaymentInfoResponse;
import com.stayease.dto.response.UserWalletResponse;
import com.stayease.dto.response.WalletTransactionResponse;
import com.stayease.service.PaymentSettingService;
import com.stayease.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final PaymentSettingService paymentSettingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserWalletResponse> getMyWallet(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(walletService.getOrCreateWallet(userId));
    }

    @GetMapping("/payment-info")
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo() {
        return ResponseEntity.ok(paymentSettingService.getPaymentInfo());
    }

    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletTransactionResponse> createDepositRequest(
            @RequestBody DepositRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(walletService.createDepositRequest(userId, request));
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<WalletTransactionResponse>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(walletService.getUserTransactions(userId, pageable));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Assuming username is email, we need to get user ID
        // This is a simplified version - you may need to adjust based on your UserDetails implementation
        return 1L; // TODO: Implement proper user ID extraction
    }
}


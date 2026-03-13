package com.stayease.controller;

import com.stayease.dto.request.UpdatePaymentSettingRequest;
import com.stayease.dto.response.*;
import com.stayease.enums.PropertyStatus;
import com.stayease.model.PaymentSetting;
import com.stayease.service.AdminService;
import com.stayease.service.PaymentSettingService;
import com.stayease.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final WalletService walletService;
    private final PaymentSettingService paymentSettingService;

    // Dashboard Stats
    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // Property Management
    @GetMapping("/properties")
    public ResponseEntity<Page<AdminPropertyResponse>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(adminService.getAllProperties(pageable));
    }

    @GetMapping("/properties/status/{status}")
    public ResponseEntity<Page<AdminPropertyResponse>> getPropertiesByStatus(
            @PathVariable PropertyStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getPropertiesByStatus(status, pageable));
    }

    @PutMapping("/properties/{id}/approve")
    public ResponseEntity<PropertyResponse> approveProperty(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveProperty(id));
    }

    @PutMapping("/properties/{id}/reject")
    public ResponseEntity<PropertyResponse> rejectProperty(
            @PathVariable Long id,
            @RequestBody(required = false) String reason) {
        return ResponseEntity.ok(adminService.rejectProperty(id, reason));
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @PutMapping("/users/{id}/toggle-active")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long id) {
        adminService.toggleUserActive(id);
        return ResponseEntity.ok().build();
    }

    // Transaction Management
    @GetMapping("/transactions")
    public ResponseEntity<Page<WalletTransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(walletService.getAllTransactions(pageable));
    }

    @GetMapping("/transactions/pending-deposits")
    public ResponseEntity<Page<WalletTransactionResponse>> getPendingDeposits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(walletService.getPendingDeposits(pageable));
    }

    @PutMapping("/transactions/confirm-deposit/{transactionCode}")
    public ResponseEntity<WalletTransactionResponse> confirmDeposit(@PathVariable String transactionCode) {
        return ResponseEntity.ok(walletService.confirmDeposit(transactionCode));
    }

    @PutMapping("/transactions/reject-deposit/{transactionCode}")
    public ResponseEntity<WalletTransactionResponse> rejectDeposit(
            @PathVariable String transactionCode,
            @RequestBody(required = false) String reason) {
        return ResponseEntity.ok(walletService.rejectDeposit(transactionCode, reason));
    }

    // Payment Settings Management
    @GetMapping("/payment-settings")
    public ResponseEntity<List<PaymentSetting>> getAllPaymentSettings() {
        return ResponseEntity.ok(paymentSettingService.getAllSettings());
    }

    @PutMapping("/payment-settings")
    public ResponseEntity<PaymentSetting> updatePaymentSetting(@RequestBody UpdatePaymentSettingRequest request) {
        return ResponseEntity.ok(paymentSettingService.updateSetting(request));
    }

    @GetMapping("/payment-settings/category/{category}")
    public ResponseEntity<List<PaymentSetting>> getPaymentSettingsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(paymentSettingService.getSettingsByCategory(category));
    }
}


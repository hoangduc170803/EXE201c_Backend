package com.stayease.controller;

import com.stayease.dto.request.CreateRefundAccountRequest;
import com.stayease.dto.request.CreateRefundRequestRequest;
import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.RefundAccountResponse;
import com.stayease.dto.response.RefundRequestResponse;
import com.stayease.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Tag(name = "Refunds", description = "Guest refund accounts and refund requests")
public class RefundController {

    private final RefundService refundService;

    @GetMapping("/accounts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my refund accounts")
    public ResponseEntity<ApiResponse<List<RefundAccountResponse>>> getMyRefundAccounts() {
        return ResponseEntity.ok(ApiResponse.success(refundService.getMyRefundAccounts()));
    }

    @PostMapping("/accounts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a refund account")
    public ResponseEntity<ApiResponse<RefundAccountResponse>> createRefundAccount(
            @Valid @RequestBody CreateRefundAccountRequest request) {
        RefundAccountResponse result = refundService.createRefundAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Refund account created", result));
    }

    @PutMapping("/accounts/{id}/default")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set default refund account")
    public ResponseEntity<ApiResponse<RefundAccountResponse>> setDefaultRefundAccount(@PathVariable Long id) {
        RefundAccountResponse result = refundService.setDefaultRefundAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Default refund account updated", result));
    }

    @DeleteMapping("/accounts/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete refund account")
    public ResponseEntity<ApiResponse<Void>> deleteRefundAccount(@PathVariable Long id) {
        refundService.deleteRefundAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Refund account deleted", null));
    }

    @PostMapping("/bookings/{bookingId}/request")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create refund request for a cancelled booking")
    public ResponseEntity<ApiResponse<RefundRequestResponse>> createRefundRequest(
            @PathVariable Long bookingId,
            @Valid @RequestBody CreateRefundRequestRequest request) {
        RefundRequestResponse result = refundService.createRefundRequest(bookingId, request);
        return ResponseEntity.ok(ApiResponse.success("Refund request created", result));
    }

    @GetMapping("/bookings/{bookingId}/request")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get refund request for a booking (if exists)")
    public ResponseEntity<ApiResponse<RefundRequestResponse>> getRefundRequestForBooking(
            @PathVariable Long bookingId
    ) {
        RefundRequestResponse result = refundService.getRefundRequestForBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my refund requests")
    public ResponseEntity<ApiResponse<Page<RefundRequestResponse>>> getMyRefundRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(refundService.getMyRefundRequests(page, size)));
    }
}


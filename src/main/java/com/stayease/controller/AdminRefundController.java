package com.stayease.controller;

import com.stayease.dto.request.UpdateRefundRequestAdminRequest;
import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.RefundRequestResponse;
import com.stayease.enums.RefundRequestStatus;
import com.stayease.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/refunds")
@RequiredArgsConstructor
@Tag(name = "Admin - Refunds", description = "Admin APIs for managing refunds")
public class AdminRefundController {

    private final RefundService refundService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List refund requests")
    public ResponseEntity<ApiResponse<Page<RefundRequestResponse>>> listRefundRequests(
            @RequestParam(required = false) RefundRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(refundService.adminListRefundRequests(status, page, size)));
    }

    @PutMapping("/{refundRequestId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update refund request status (APPROVED/REJECTED/PAID)")
    public ResponseEntity<ApiResponse<RefundRequestResponse>> updateRefundRequest(
            @PathVariable Long refundRequestId,
            @Valid @RequestBody UpdateRefundRequestAdminRequest request
    ) {
        RefundRequestResponse result = refundService.adminUpdateRefundRequest(refundRequestId, request);
        return ResponseEntity.ok(ApiResponse.success("Refund request updated", result));
    }
}


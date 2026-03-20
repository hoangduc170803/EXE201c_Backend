package com.stayease.controller;

import com.stayease.dto.request.ConfirmBankStatementLineRequest;
import com.stayease.dto.request.RejectBankStatementLineRequest;
import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.BankStatementImportResponse;
import com.stayease.dto.response.BankStatementLineDto;
import com.stayease.dto.response.BankStatementSessionResponse;
import com.stayease.model.User;
import com.stayease.repository.UserRepository;
import com.stayease.service.BankStatementReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/bank-statements")
@RequiredArgsConstructor
@Tag(name = "Admin - Bank Statements", description = "Admin APIs for bank statement reconciliation (no 3rd-party)")
public class AdminBankStatementController {

    private final BankStatementReconciliationService reconciliationService;
    private final UserRepository userRepository;

    @PostMapping(value = "/vietcombank/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Import Vietcombank statement Excel (.xlsx) - preview parsing + auto match")
    public ResponseEntity<ApiResponse<BankStatementImportResponse>> importVietcombankExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "previewLimit", defaultValue = "200") int previewLimit,
            Authentication authentication
    ) {
        User admin = resolveAdmin(authentication);
        BankStatementImportResponse result = reconciliationService.importVietcombankExcel(
                file,
                previewLimit,
                admin.getId(),
                admin.getEmail()
        );
        return ResponseEntity.ok(ApiResponse.success("Imported statement preview", result));
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List uploaded statement sessions")
    public ResponseEntity<ApiResponse<Page<BankStatementSessionResponse>>> listSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<BankStatementSessionResponse> sessions = reconciliationService.listSessions(page, size);
        return ResponseEntity.ok(ApiResponse.success("Fetched sessions", sessions));
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get detail + lines for a statement session")
    public ResponseEntity<ApiResponse<BankStatementImportResponse>> getSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success("Fetched session", reconciliationService.getSession(sessionId)));
    }

    @PostMapping("/sessions/{sessionId}/lines/{lineId}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm a statement line matches a booking and mark booking as paid")
    public ResponseEntity<ApiResponse<BankStatementLineDto>> confirmLine(
            @PathVariable Long sessionId,
            @PathVariable Long lineId,
            @Valid @RequestBody ConfirmBankStatementLineRequest request,
            Authentication authentication
    ) {
        User admin = resolveAdmin(authentication);
        BankStatementLineDto dto = reconciliationService.confirmLine(
                sessionId,
                lineId,
                request.getBookingId(),
                request.getBookingCode(),
                request.getBankReference(),
                request.getNote(),
                admin.getId()
        );
        return ResponseEntity.ok(ApiResponse.success("Đã xác nhận dòng sao kê", dto));
    }

    @PostMapping("/sessions/{sessionId}/lines/{lineId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject a statement line (no booking match)")
    public ResponseEntity<ApiResponse<BankStatementLineDto>> rejectLine(
            @PathVariable Long sessionId,
            @PathVariable Long lineId,
            @Valid @RequestBody RejectBankStatementLineRequest request,
            Authentication authentication
    ) {
        User admin = resolveAdmin(authentication);
        BankStatementLineDto dto = reconciliationService.rejectLine(sessionId, lineId, request.getNote(), admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã reject dòng sao kê", dto));
    }

    @PostMapping("/confirm-booking-payment/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually confirm a booking payment as PAID (after statement review)")
    public ResponseEntity<ApiResponse<Void>> confirmBookingPayment(
            @PathVariable Long bookingId,
            @RequestParam(required = false) String bankReference
    ) {
        reconciliationService.confirmMatchedPayment(bookingId, bankReference);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", null));
    }

    private User resolveAdmin(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new EntityNotFoundException("Admin not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }
}

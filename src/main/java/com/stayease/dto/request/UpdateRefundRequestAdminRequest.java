package com.stayease.dto.request;

import com.stayease.enums.RefundRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRefundRequestAdminRequest {

    @NotNull
    private RefundRequestStatus status; // APPROVED, REJECTED, PAID

    private String adminNote;

    /** Only required when marking PAID */
    private String payoutReference;
}


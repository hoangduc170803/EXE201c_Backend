package com.stayease.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRefundRequestRequest {

    @NotNull
    private Long refundAccountId;

    private String reason;
}


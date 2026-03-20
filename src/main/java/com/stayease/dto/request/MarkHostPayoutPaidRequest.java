package com.stayease.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkHostPayoutPaidRequest {
    @NotBlank
    private String payoutReference;

    private String adminNote;
}


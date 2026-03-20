package com.stayease.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectBankStatementLineRequest {
    @NotBlank
    private String note;
}

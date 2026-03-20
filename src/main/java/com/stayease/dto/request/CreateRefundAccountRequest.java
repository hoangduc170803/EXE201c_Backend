package com.stayease.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRefundAccountRequest {

    @NotBlank
    private String bankName;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountHolder;

    private Boolean isDefault;
}


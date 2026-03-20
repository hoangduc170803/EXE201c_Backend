package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAccountResponse {
    private Long id;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private Boolean isDefault;
}


package com.stayease.dto.request;

import lombok.Data;

@Data
public class ConfirmBankStatementLineRequest {
    private Long bookingId;
    private String bookingCode;
    private String bankReference;
    private String note;
}

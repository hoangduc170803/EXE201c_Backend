package com.stayease.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AutoCreateHostPayoutRequest {
    @NotNull
    private Long hostId;

    private String adminNote;
}


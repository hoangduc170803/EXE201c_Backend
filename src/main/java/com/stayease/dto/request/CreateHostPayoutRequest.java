package com.stayease.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateHostPayoutRequest {
    @NotNull
    private Long hostId;

    @NotEmpty
    private List<Long> bookingIds;

    private String adminNote;
}


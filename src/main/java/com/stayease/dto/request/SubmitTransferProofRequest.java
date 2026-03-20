package com.stayease.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTransferProofRequest {

    @NotBlank(message = "transferProofImageUrl is required")
    private String transferProofImageUrl;

    private String transferReference;
}


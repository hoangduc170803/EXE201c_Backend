package com.stayease.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositProofRequest {

    private String transferReference; // Mã tham chiếu ngân hàng (VD: FT26071234567)
    private String proofImageUrl;     // URL ảnh biên lai chuyển khoản
}


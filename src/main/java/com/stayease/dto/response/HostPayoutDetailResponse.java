package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostPayoutDetailResponse {
    private HostPayoutResponse payout;
    private List<SettlementDueItemResponse> items;
}


package com.stayease.dto.response;

import com.stayease.enums.BankStatementMatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementLineDto {
    private int rowNumber;
    private Long lineId;
    private LocalDate transactionDate;
    private String description;

    /** Money in (credit) */
    private BigDecimal amountIn;

    /** Money out (debit) */
    private BigDecimal amountOut;

    /** Reference extracted from description (e.g., BKXXXX...) */
    private String extractedBookingCode;

    /** True if amount + code matches a booking */
    private boolean matched;
    private Long matchedBookingId;
    private String matchedBookingCode;
    private BankStatementMatchStatus status;
    private boolean confirmed;
    private String note;
}

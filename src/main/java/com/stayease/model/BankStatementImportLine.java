package com.stayease.model;

import com.stayease.enums.BankStatementMatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_statement_import_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankStatementImportLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private BankStatementImportSession session;

    @Column(name = "row_index")
    private Integer rowNumber;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "amount_in", precision = 18, scale = 2)
    private BigDecimal amountIn;

    @Column(name = "amount_out", precision = 18, scale = 2)
    private BigDecimal amountOut;

    @Column(name = "extracted_booking_code", length = 32)
    private String extractedBookingCode;

    @Column(name = "matched_booking_id")
    private Long matchedBookingId;

    @Column(name = "matched_booking_code", length = 32)
    private String matchedBookingCode;

    @Column(name = "amount_matched")
    private Boolean amountMatched;

    @Column(name = "bank_reference", length = 255)
    private String bankReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", length = 30)
    private BankStatementMatchStatus status;

    @Column(name = "confirmed_by_user_id")
    private Long confirmedByUserId;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "note", length = 1000)
    private String note;
}

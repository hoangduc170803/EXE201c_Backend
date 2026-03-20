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
public class BankStatementImportResponse {
    private Long sessionId;
    private String bank;
    private String filename;
    private int totalRows;
    private int parsedLines;
    private int matchedLines;
    private int confirmedLines;
    private List<BankStatementLineDto> lines;
    private String note;
}

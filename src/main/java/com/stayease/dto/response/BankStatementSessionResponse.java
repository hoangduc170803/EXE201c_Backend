package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementSessionResponse {
    private Long id;
    private String bank;
    private String filename;
    private Integer totalRows;
    private Integer parsedLines;
    private Integer matchedLines;
    private Integer confirmedLines;
    private Long uploadedByUserId;
    private String uploadedByEmail;
    private LocalDateTime createdAt;
}


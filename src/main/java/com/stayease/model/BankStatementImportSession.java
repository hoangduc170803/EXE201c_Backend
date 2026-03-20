package com.stayease.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bank_statement_import_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankStatementImportSession extends BaseEntity {

    @Column(name = "bank", length = 100)
    private String bank;

    @Column(name = "filename", length = 255)
    private String filename;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "uploaded_by_email", length = 255)
    private String uploadedByEmail;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "parsed_lines")
    private Integer parsedLines;

    @Column(name = "matched_lines")
    private Integer matchedLines;

    @Column(name = "confirmed_lines")
    private Integer confirmedLines;

    @Column(name = "note", length = 500)
    private String note;

    @OneToMany(mappedBy = "session")
    private List<BankStatementImportLine> lines = new ArrayList<>();
}


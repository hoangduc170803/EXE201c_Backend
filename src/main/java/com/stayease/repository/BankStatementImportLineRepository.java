package com.stayease.repository;

import com.stayease.enums.BankStatementMatchStatus;
import com.stayease.model.BankStatementImportLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankStatementImportLineRepository extends JpaRepository<BankStatementImportLine, Long> {

    List<BankStatementImportLine> findBySessionIdOrderByRowNumberAsc(Long sessionId);

    Page<BankStatementImportLine> findBySessionId(Long sessionId, Pageable pageable);

    long countBySessionIdAndStatus(Long sessionId, BankStatementMatchStatus status);
}


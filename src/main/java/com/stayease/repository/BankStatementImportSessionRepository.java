package com.stayease.repository;

import com.stayease.model.BankStatementImportSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStatementImportSessionRepository extends JpaRepository<BankStatementImportSession, Long> {
}


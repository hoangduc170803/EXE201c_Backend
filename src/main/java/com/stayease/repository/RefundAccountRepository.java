package com.stayease.repository;

import com.stayease.model.RefundAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundAccountRepository extends JpaRepository<RefundAccount, Long> {
    List<RefundAccount> findByUserIdOrderByCreatedAtDesc(Long userId);
}


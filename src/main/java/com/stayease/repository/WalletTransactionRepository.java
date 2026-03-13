package com.stayease.repository;

import com.stayease.model.WalletTransaction;
import com.stayease.enums.TransactionType;
import com.stayease.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByUserId(Long userId, Pageable pageable);

    Page<WalletTransaction> findByWalletId(Long walletId, Pageable pageable);

    Optional<WalletTransaction> findByTransactionCode(String transactionCode);

    List<WalletTransaction> findByReferenceId(String referenceId);

    Page<WalletTransaction> findByUserIdAndTransactionType(Long userId, TransactionType type, Pageable pageable);

    Page<WalletTransaction> findByUserIdAndStatus(Long userId, TransactionStatus status, Pageable pageable);

    @Query("SELECT t FROM WalletTransaction t WHERE t.user.id = :userId AND t.status = :status ORDER BY t.createdAt DESC")
    List<WalletTransaction> findRecentByUserIdAndStatus(@Param("userId") Long userId,
                                                          @Param("status") TransactionStatus status);

    @Query("SELECT t FROM WalletTransaction t ORDER BY t.createdAt DESC")
    Page<WalletTransaction> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT t FROM WalletTransaction t WHERE t.transactionType = :type AND t.status = :status ORDER BY t.createdAt DESC")
    Page<WalletTransaction> findByTransactionTypeAndStatus(
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable);
}


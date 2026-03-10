package com.stayease.service;

import com.stayease.dto.request.DepositRequest;
import com.stayease.dto.response.UserWalletResponse;
import com.stayease.dto.response.WalletTransactionResponse;
import com.stayease.enums.TransactionStatus;
import com.stayease.enums.TransactionType;
import com.stayease.model.User;
import com.stayease.model.UserWallet;
import com.stayease.model.WalletTransaction;
import com.stayease.repository.UserRepository;
import com.stayease.repository.UserWalletRepository;
import com.stayease.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserWalletResponse getOrCreateWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserWallet newWallet = UserWallet.builder()
                            .user(user)
                            .balance(BigDecimal.ZERO)
                            .totalDeposited(BigDecimal.ZERO)
                            .totalSpent(BigDecimal.ZERO)
                            .isActive(true)
                            .currency("VND")
                            .build();
                    return walletRepository.save(newWallet);
                });

        return mapWalletToResponse(wallet);
    }

    @Transactional
    public WalletTransactionResponse createDepositRequest(Long userId, DepositRequest request) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        User user = wallet.getUser();

        // Create transaction with PENDING status
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .user(user)
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description("Nạp tiền vào ví")
                .paymentMethod(request.getPaymentMethod())
                .transactionCode(generateTransactionCode())
                .balanceBefore(wallet.getBalance())
                .balanceAfter(wallet.getBalance()) // Will update after confirmation
                .notes(request.getTransactionNote())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Created deposit request: {} for user: {}", transaction.getTransactionCode(), userId);

        return mapTransactionToResponse(transaction);
    }

    @Transactional
    public WalletTransactionResponse confirmDeposit(String transactionCode) {
        WalletTransaction transaction = transactionRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction already processed");
        }

        UserWallet wallet = transaction.getWallet();

        // Update wallet balance
        wallet.deposit(transaction.getAmount());
        walletRepository.save(wallet);

        // Update transaction
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction = transactionRepository.save(transaction);

        log.info("Confirmed deposit: {} for user: {}", transactionCode, wallet.getUser().getId());

        return mapTransactionToResponse(transaction);
    }

    @Transactional
    public WalletTransactionResponse processPayment(Long userId, BigDecimal amount, String description, String referenceId) {
        UserWallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (!wallet.hasBalance(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.spend(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .amount(amount)
                .transactionType(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .description(description)
                .referenceId(referenceId)
                .paymentMethod("WALLET")
                .transactionCode(generateTransactionCode())
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Processed payment: {} for user: {}", transaction.getTransactionCode(), userId);

        return mapTransactionToResponse(transaction);
    }

    public Page<WalletTransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        Page<WalletTransaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return transactions.map(this::mapTransactionToResponse);
    }

    public Page<WalletTransactionResponse> getAllTransactions(Pageable pageable) {
        Page<WalletTransaction> transactions = transactionRepository.findAllOrderByCreatedAtDesc(pageable);
        return transactions.map(this::mapTransactionToResponse);
    }

    private UserWalletResponse mapWalletToResponse(UserWallet wallet) {
        return UserWalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .userName(wallet.getUser().getFirstName() + " " + wallet.getUser().getLastName())
                .userEmail(wallet.getUser().getEmail())
                .balance(wallet.getBalance())
                .totalDeposited(wallet.getTotalDeposited())
                .totalSpent(wallet.getTotalSpent())
                .isActive(wallet.getIsActive())
                .currency(wallet.getCurrency())
                .build();
    }

    private WalletTransactionResponse mapTransactionToResponse(WalletTransaction transaction) {
        return WalletTransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .userName(transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .referenceId(transaction.getReferenceId())
                .paymentMethod(transaction.getPaymentMethod())
                .transactionCode(transaction.getTransactionCode())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .notes(transaction.getNotes())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private String generateTransactionCode() {
        return "WTX-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}


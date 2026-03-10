package com.stayease.repository;

import com.stayease.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {

    Optional<UserWallet> findByUserId(Long userId);

    Optional<UserWallet> findByUserIdAndIsActiveTrue(Long userId);
}


package com.stayease.repository;

import com.stayease.model.PaymentSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentSettingRepository extends JpaRepository<PaymentSetting, Long> {

    Optional<PaymentSetting> findBySettingKey(String settingKey);

    List<PaymentSetting> findByCategory(String category);

    List<PaymentSetting> findByIsActiveTrue();

    List<PaymentSetting> findByCategoryAndIsActiveTrue(String category);
}


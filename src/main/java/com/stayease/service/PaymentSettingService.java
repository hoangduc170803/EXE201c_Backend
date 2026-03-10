package com.stayease.service;

import com.stayease.dto.request.UpdatePaymentSettingRequest;
import com.stayease.dto.response.PaymentInfoResponse;
import com.stayease.model.PaymentSetting;
import com.stayease.repository.PaymentSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSettingService {

    private final PaymentSettingRepository paymentSettingRepository;

    public PaymentInfoResponse getPaymentInfo() {
        return PaymentInfoResponse.builder()
                .bankName(getSettingValue(PaymentSetting.BANK_NAME))
                .bankAccountNumber(getSettingValue(PaymentSetting.BANK_ACCOUNT_NUMBER))
                .bankAccountHolder(getSettingValue(PaymentSetting.BANK_ACCOUNT_HOLDER))
                .bankBranch(getSettingValue(PaymentSetting.BANK_BRANCH))
                .qrCodeUrl(getSettingValue(PaymentSetting.QR_CODE_URL))
                .paymentNotes(getSettingValue(PaymentSetting.PAYMENT_NOTES))
                .build();
    }

    public String getSettingValue(String key) {
        return paymentSettingRepository.findBySettingKey(key)
                .map(PaymentSetting::getSettingValue)
                .orElse(null);
    }

    @Transactional
    public PaymentSetting updateSetting(UpdatePaymentSettingRequest request) {
        PaymentSetting setting = paymentSettingRepository.findBySettingKey(request.getSettingKey())
                .orElse(PaymentSetting.builder()
                        .settingKey(request.getSettingKey())
                        .build());

        setting.setSettingValue(request.getSettingValue());
        setting.setDescription(request.getDescription());
        setting.setCategory(request.getCategory());

        if (request.getIsActive() != null) {
            setting.setIsActive(request.getIsActive());
        }

        setting = paymentSettingRepository.save(setting);
        log.info("Updated payment setting: {}", request.getSettingKey());

        return setting;
    }

    public List<PaymentSetting> getAllSettings() {
        return paymentSettingRepository.findAll();
    }

    public List<PaymentSetting> getSettingsByCategory(String category) {
        return paymentSettingRepository.findByCategory(category);
    }

    @Transactional
    public void initializeDefaultSettings() {
        if (paymentSettingRepository.count() == 0) {
            log.info("Initializing default payment settings...");

            createSettingIfNotExists(PaymentSetting.BANK_NAME, "Vietcombank", "Tên ngân hàng", "BANK_INFO");
            createSettingIfNotExists(PaymentSetting.BANK_ACCOUNT_NUMBER, "1234567890", "Số tài khoản", "BANK_INFO");
            createSettingIfNotExists(PaymentSetting.BANK_ACCOUNT_HOLDER, "CONG TY STAYEASE", "Chủ tài khoản", "BANK_INFO");
            createSettingIfNotExists(PaymentSetting.BANK_BRANCH, "Chi nhánh TP.HCM", "Chi nhánh", "BANK_INFO");
            createSettingIfNotExists(PaymentSetting.QR_CODE_URL, "/uploads/payment/qr-code.png", "URL mã QR", "QR_CODE");
            createSettingIfNotExists(PaymentSetting.PAYMENT_NOTES,
                "Vui lòng ghi rõ mã giao dịch trong nội dung chuyển khoản",
                "Ghi chú thanh toán", "PAYMENT_INFO");

            log.info("Default payment settings initialized");
        }
    }

    private void createSettingIfNotExists(String key, String value, String description, String category) {
        if (!paymentSettingRepository.findBySettingKey(key).isPresent()) {
            PaymentSetting setting = PaymentSetting.builder()
                    .settingKey(key)
                    .settingValue(value)
                    .description(description)
                    .category(category)
                    .isActive(true)
                    .build();
            paymentSettingRepository.save(setting);
        }
    }
}


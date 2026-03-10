package com.stayease.service;

import com.stayease.dto.request.CreateSubscriptionRequest;
import com.stayease.dto.response.PostingPackageResponse;
import com.stayease.dto.response.PropertyPackageSubscriptionResponse;
import com.stayease.enums.PackageSubscriptionStatus;
import com.stayease.model.PostingPackage;
import com.stayease.model.Property;
import com.stayease.model.PropertyPackageSubscription;
import com.stayease.repository.PostingPackageRepository;
import com.stayease.repository.PropertyPackageSubscriptionRepository;
import com.stayease.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostingPackageService {

    private final PostingPackageRepository postingPackageRepository;
    private final PropertyPackageSubscriptionRepository subscriptionRepository;
    private final PropertyRepository propertyRepository;
    private final WalletService walletService;

    public List<PostingPackageResponse> getAllActivePackages() {
        List<PostingPackage> packages = postingPackageRepository.findAllByIsActiveTrueOrderByPriorityLevelDesc();
        return packages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PostingPackageResponse getPackageById(Long id) {
        PostingPackage pkg = postingPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));
        return mapToResponse(pkg);
    }

    @Transactional
    public PropertyPackageSubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        // Validate property exists
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + request.getPropertyId()));

        // Validate package exists
        PostingPackage pkg = postingPackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + request.getPackageId()));

        if (!pkg.getIsActive()) {
            throw new IllegalArgumentException("Package is not active");
        }

        // Create subscription
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = now.plusDays(pkg.getDurationDays() != null ? pkg.getDurationDays() : 30);
        String transactionId = generateTransactionId();

        PropertyPackageSubscription subscription = PropertyPackageSubscription.builder()
                .property(property)
                .postingPackage(pkg)
                .startAt(now)
                .endAt(endAt)
                .isActive(false) // Will be activated after payment confirmation
                .status(PackageSubscriptionStatus.PENDING)
                .transactionId(transactionId)
                .build();

        subscription = subscriptionRepository.save(subscription);

        // If package has price, process payment from wallet
        if (pkg.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            try {
                walletService.processPayment(
                    property.getHost().getId(),
                    pkg.getPrice(),
                    "Thanh toán gói đăng tin: " + pkg.getName(),
                    transactionId
                );

                // Auto-activate subscription after successful payment
                subscription.setStatus(PackageSubscriptionStatus.ACTIVE);
                subscription.setIsActive(true);
                subscription = subscriptionRepository.save(subscription);

                log.info("Subscription auto-activated with wallet payment: {}", transactionId);
            } catch (IllegalArgumentException e) {
                // If payment fails, keep subscription as PENDING
                log.warn("Wallet payment failed for subscription: {}. Error: {}", transactionId, e.getMessage());
                throw new IllegalArgumentException("Số dư ví không đủ. Vui lòng nạp thêm tiền vào ví.");
            }
        } else {
            // Free package - auto activate
            subscription.setStatus(PackageSubscriptionStatus.ACTIVE);
            subscription.setIsActive(true);
            subscription = subscriptionRepository.save(subscription);
        }

        log.info("Created subscription with transaction ID: {}", subscription.getTransactionId());

        return mapSubscriptionToResponse(subscription);
    }

    @Transactional
    public PropertyPackageSubscriptionResponse confirmPayment(String transactionId) {
        PropertyPackageSubscription subscription = subscriptionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with transaction ID: " + transactionId));

        subscription.setStatus(PackageSubscriptionStatus.ACTIVE);
        subscription.setIsActive(true);
        subscription = subscriptionRepository.save(subscription);

        log.info("Payment confirmed for transaction ID: {}", transactionId);
        return mapSubscriptionToResponse(subscription);
    }

    public List<PropertyPackageSubscriptionResponse> getPropertySubscriptions(Long propertyId) {
        List<PropertyPackageSubscription> subscriptions = subscriptionRepository.findByPropertyId(propertyId);
        return subscriptions.stream()
                .map(this::mapSubscriptionToResponse)
                .collect(Collectors.toList());
    }

    private PostingPackageResponse mapToResponse(PostingPackage pkg) {
        List<String> features = null;
        if (pkg.getFeatures() != null && !pkg.getFeatures().isEmpty()) {
            features = Arrays.asList(pkg.getFeatures().split(","));
        }

        // Determine badge and popularity
        String badge = null;
        Boolean isPopular = false;

        if (pkg.getPriorityLevel() != null) {
            if (pkg.getPriorityLevel() >= 3) {
                badge = "MOST POPULAR";
                isPopular = true;
            } else if (pkg.getPriorityLevel() >= 2) {
                badge = "RECOMMENDED";
            }
        }

        return PostingPackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .slug(pkg.getSlug())
                .description(pkg.getDescription())
                .price(pkg.getPrice())
                .durationDays(pkg.getDurationDays())
                .priorityLevel(pkg.getPriorityLevel())
                .isActive(pkg.getIsActive())
                .features(features)
                .isPopular(isPopular)
                .badge(badge)
                .build();
    }

    private PropertyPackageSubscriptionResponse mapSubscriptionToResponse(PropertyPackageSubscription subscription) {
        return PropertyPackageSubscriptionResponse.builder()
                .id(subscription.getId())
                .postingPackage(mapToResponse(subscription.getPostingPackage()))
                .propertyId(subscription.getProperty().getId())
                .startAt(subscription.getStartAt())
                .endAt(subscription.getEndAt())
                .isActive(subscription.getIsActive())
                .transactionId(subscription.getTransactionId())
                .status(subscription.getStatus())
                .createdAt(subscription.getCreatedAt())
                .build();
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}


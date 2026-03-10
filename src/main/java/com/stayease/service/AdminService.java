package com.stayease.service;

import com.stayease.dto.response.*;
import com.stayease.enums.PackageSubscriptionStatus;
import com.stayease.enums.PropertyStatus;
import com.stayease.enums.TransactionStatus;
import com.stayease.model.*;
import com.stayease.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final WalletTransactionRepository transactionRepository;
    private final PropertyPackageSubscriptionRepository subscriptionRepository;
    private final UserWalletRepository walletRepository;

    public AdminDashboardStatsResponse getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();

        // User stats
        Long totalUsers = userRepository.count();
        Long totalHosts = 0L;
        Long totalGuests = 0L;
        try {
            totalHosts = userRepository.countByRoles_Name("ROLE_HOST");
            totalGuests = userRepository.countByRoles_Name("ROLE_GUEST");
        } catch (Exception e) {
            // Methods might not exist yet
        }

        // Property stats
        Long totalProperties = propertyRepository.count();
        Long activeProperties = 0L;
        Long pendingProperties = 0L;
        try {
            activeProperties = propertyRepository.countByStatus(PropertyStatus.ACTIVE);
            pendingProperties = propertyRepository.countByStatus(PropertyStatus.PENDING);
        } catch (Exception e) {
            // Methods might not exist yet
        }

        // Calculate total revenue from completed transactions
        BigDecimal totalRevenue = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Subscription stats
        Long activeSubscriptions = 0L;
        Long expiredSubscriptions = 0L;
        Long pendingSubscriptions = 0L;
        try {
            activeSubscriptions = subscriptionRepository.countByStatus(PackageSubscriptionStatus.ACTIVE);
            expiredSubscriptions = subscriptionRepository.countByStatus(PackageSubscriptionStatus.EXPIRED);
            pendingSubscriptions = subscriptionRepository.countByStatus(PackageSubscriptionStatus.PENDING);
        } catch (Exception e) {
            // Methods might not exist yet
        }

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalHosts(totalHosts)
                .totalGuests(totalGuests)
                .newUsersThisMonth(0L) // TODO: Implement date filtering
                .totalProperties(totalProperties)
                .activeProperties(activeProperties)
                .pendingProperties(pendingProperties)
                .newPropertiesThisMonth(0L) // TODO: Implement date filtering
                .totalRevenue(totalRevenue)
                .monthlyRevenue(BigDecimal.ZERO) // TODO: Implement month filtering
                .pendingPayments(BigDecimal.ZERO) // TODO: Calculate pending
                .totalTransactions(transactionRepository.count())
                .activeSubscriptions(activeSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .pendingSubscriptions(pendingSubscriptions)
                .todayBookings(0L) // TODO: Implement if needed
                .todayTransactions(0L) // TODO: Implement date filtering
                .build();
    }

    public Page<AdminPropertyResponse> getAllProperties(Pageable pageable) {
        Page<Property> properties = propertyRepository.findAll(pageable);
        return properties.map(this::mapPropertyToAdminResponse);
    }

    public Page<AdminPropertyResponse> getPropertiesByStatus(PropertyStatus status, Pageable pageable) {
        Page<Property> properties;
        try {
            properties = propertyRepository.findByStatus(status, pageable);
        } catch (Exception e) {
            // Fallback if method doesn't exist
            properties = propertyRepository.findByStatusAndIsDeletedFalse(status, pageable);
        }
        return properties.map(this::mapPropertyToAdminResponse);
    }

    @Transactional
    public PropertyResponse approveProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setStatus(PropertyStatus.ACTIVE);
        property = propertyRepository.save(property);

        log.info("Property approved: {}", propertyId);

        // Return basic response (you can create a mapper if needed)
        return null; // TODO: Implement proper response mapper
    }

    @Transactional
    public PropertyResponse rejectProperty(Long propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setStatus(PropertyStatus.INACTIVE);
        property = propertyRepository.save(property);

        log.info("Property rejected: {} - Reason: {}", propertyId, reason);

        return null; // TODO: Implement proper response mapper
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapUserToAdminResponse);
    }

    @Transactional
    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        log.info("User active status toggled: {} -> {}", userId, user.getIsActive());
    }

    private AdminPropertyResponse mapPropertyToAdminResponse(Property property) {
        // Get active subscription if exists
        String currentPackage = null;
        LocalDateTime subscriptionEndDate = null;

        // This is simplified - you may want to optimize with a query
        var subscriptions = subscriptionRepository.findByPropertyId(property.getId());
        var activeSubscription = subscriptions.stream()
                .filter(s -> s.getStatus() == PackageSubscriptionStatus.ACTIVE)
                .findFirst();

        if (activeSubscription.isPresent()) {
            currentPackage = activeSubscription.get().getPostingPackage().getName();
            subscriptionEndDate = activeSubscription.get().getEndAt();
        }

        return AdminPropertyResponse.builder()
                .id(property.getId())
                .title(property.getTitle())
                .propertyType(property.getPropertyType().name())
                .rentalType(property.getRentalType().name())
                .address(property.getAddress())
                .city(property.getCity())
                .pricePerNight(property.getPricePerNight())
                .pricePerMonth(property.getPricePerMonth())
                .status(property.getStatus())
                .hostId(property.getHost().getId())
                .hostName(property.getHost().getFirstName() + " " + property.getHost().getLastName())
                .hostEmail(property.getHost().getEmail())
                .currentPackage(currentPackage)
                .subscriptionEndDate(subscriptionEndDate)
                .viewCount(property.getViewCount())
                .totalReviews(property.getTotalReviews())
                .averageRating(property.getAverageRating())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }

    private AdminUserResponse mapUserToAdminResponse(User user) {
        // Get wallet balance if exists
        BigDecimal walletBalance = walletRepository.findByUserId(user.getId())
                .map(UserWallet::getBalance)
                .orElse(BigDecimal.ZERO);

        // Get property count if host
        Long totalProperties = 0L;
        try {
            totalProperties = propertyRepository.countByHostId(user.getId());
        } catch (Exception e) {
            // Ignore if method doesn't exist yet
        }

        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(null) // TODO: Add to User model if needed
                .profilePictureUrl(null) // TODO: Add to User model if needed
                .bio(user.getBio())
                .city(user.getCity())
                .country(user.getCountry())
                .isEmailVerified(false) // TODO: Add to User model if needed
                .isPhoneVerified(false) // TODO: Add to User model if needed
                .isProfileComplete(false) // TODO: Add to User model if needed
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream().map(r -> r.getName().toString()).collect(Collectors.toSet()))
                .totalProperties(totalProperties)
                .walletBalance(walletBalance)
                .totalBookingsAsHost(0L) // TODO: Implement if needed
                .totalBookingsAsGuest(0L) // TODO: Implement if needed
                .createdAt(user.getCreatedAt())
                .lastLoginAt(null) // TODO: Track last login
                .build();
    }
}



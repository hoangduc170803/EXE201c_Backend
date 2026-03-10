package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePictureUrl;
    private String bio;
    private String city;
    private String country;

    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Boolean isProfileComplete;
    private Boolean isActive;

    private Set<String> roles;

    // Host-specific stats (if user is host)
    private Long totalProperties;
    private BigDecimal walletBalance;
    private Long totalBookingsAsHost;

    // Guest-specific stats
    private Long totalBookingsAsGuest;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}


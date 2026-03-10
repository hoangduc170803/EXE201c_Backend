package com.stayease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsResponse {

    // User stats
    private Long totalUsers;
    private Long totalHosts;
    private Long totalGuests;
    private Long newUsersThisMonth;

    // Property stats
    private Long totalProperties;
    private Long activeProperties;
    private Long pendingProperties;
    private Long newPropertiesThisMonth;

    // Financial stats
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal pendingPayments;
    private Long totalTransactions;

    // Package subscription stats
    private Long activeSubscriptions;
    private Long expiredSubscriptions;
    private Long pendingSubscriptions;

    // Recent activity counts
    private Long todayBookings;
    private Long todayTransactions;
}


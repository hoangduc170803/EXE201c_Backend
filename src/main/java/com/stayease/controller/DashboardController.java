package com.stayease.controller;

import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.DashboardStatsResponse;
import com.stayease.dto.response.MonthlyRevenueResponse;
import com.stayease.service.CustomUserDetailsService.UserPrincipal;
import com.stayease.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Host dashboard statistics API")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get host dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        DashboardStatsResponse stats = dashboardService.getHostDashboardStats(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/monthly-revenue")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get monthly revenue for the year")
    public ResponseEntity<ApiResponse<List<MonthlyRevenueResponse>>> getMonthlyRevenue(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) Integer year) {
        List<MonthlyRevenueResponse> monthlyRevenue = dashboardService.getHostMonthlyRevenue(currentUser.getId(), year);
        return ResponseEntity.ok(ApiResponse.success(monthlyRevenue));
    }
}


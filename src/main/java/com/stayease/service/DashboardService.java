package com.stayease.service;

import com.stayease.dto.response.DashboardStatsResponse;
import com.stayease.dto.response.MonthlyRevenueResponse;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.PropertyRepository;
import com.stayease.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final ReviewRepository reviewRepository;

    public DashboardService(BookingRepository bookingRepository,
                           PropertyRepository propertyRepository,
                           ReviewRepository reviewRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
    }

    public DashboardStatsResponse getHostDashboardStats(Long hostId) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int previousYear = currentMonth == 1 ? currentYear - 1 : currentYear;

        // 1. Doanh thu tháng này
        BigDecimal monthlyRevenue = bookingRepository.sumRevenueByHostIdAndMonth(hostId, currentMonth, currentYear);
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;

        BigDecimal previousMonthRevenue = bookingRepository.sumRevenueByHostIdAndMonth(hostId, previousMonth, previousYear);
        if (previousMonthRevenue == null) previousMonthRevenue = BigDecimal.ZERO;

        Double monthlyRevenueChangePercent = calculatePercentageChange(previousMonthRevenue, monthlyRevenue);

        // 2. Tổng lượt khách tháng này
        Long totalGuestsThisMonth = bookingRepository.sumGuestsByHostIdAndMonth(hostId, currentMonth, currentYear);
        if (totalGuestsThisMonth == null) totalGuestsThisMonth = 0L;

        Long totalGuestsPreviousMonth = bookingRepository.sumGuestsByHostIdAndMonth(hostId, previousMonth, previousYear);
        if (totalGuestsPreviousMonth == null) totalGuestsPreviousMonth = 0L;

        Double totalGuestsChangePercent = calculatePercentageChange(
                BigDecimal.valueOf(totalGuestsPreviousMonth),
                BigDecimal.valueOf(totalGuestsThisMonth)
        );

        // 3. Tổng lượt xem (không tính % vì không có dữ liệu lịch sử theo tháng)
        Long totalViews = propertyRepository.sumViewCountByHostId(hostId);
        if (totalViews == null) totalViews = 0L;

        // 4. Đánh giá trung bình
        BigDecimal averageRatingBD = reviewRepository.getAverageRatingByHostId(hostId);
        Double averageRating = averageRatingBD != null ? averageRatingBD.doubleValue() : 0.0;

        Long totalReviews = reviewRepository.countByHostId(hostId);
        if (totalReviews == null) totalReviews = 0L;

        return DashboardStatsResponse.builder()
                .monthlyRevenue(monthlyRevenue)
                .monthlyRevenueChangePercent(monthlyRevenueChangePercent)
                .totalGuests(totalGuestsThisMonth)
                .totalGuestsChangePercent(totalGuestsChangePercent)
                .totalViews(totalViews)
                .totalViewsChangePercent(0.0) // Không có dữ liệu để tính
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }

    public List<MonthlyRevenueResponse> getHostMonthlyRevenue(Long hostId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        List<Object[]> results = bookingRepository.findMonthlyRevenueByHostIdAndYear(hostId, year);

        // Create a map of month -> revenue
        Map<Integer, BigDecimal> revenueMap = results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(), // month
                        row -> (BigDecimal) row[2] // revenue
                ));

        // Create response for all 12 months
        List<MonthlyRevenueResponse> monthlyRevenues = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = revenueMap.getOrDefault(month, BigDecimal.ZERO);
            monthlyRevenues.add(MonthlyRevenueResponse.builder()
                    .month(month)
                    .year(year)
                    .monthName("T" + month)
                    .revenue(revenue)
                    .build());
        }

        return monthlyRevenues;
    }

    private Double calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            // If old value is 0, return 0 if new value is also 0, otherwise 100%
            return newValue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 : 100.0;
        }

        BigDecimal change = newValue.subtract(oldValue);
        BigDecimal percentChange = change.divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return percentChange.doubleValue();
    }
}


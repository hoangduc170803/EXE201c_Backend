package com.stayease.controller;

import com.stayease.dto.request.CreateBookingRequest;
import com.stayease.dto.response.ApiResponse;
import com.stayease.dto.response.BookingResponse;
import com.stayease.dto.response.BookingStatsResponse;
import com.stayease.dto.response.BookingCalendarResponse;
import com.stayease.dto.response.PageResponse;
import com.stayease.service.BookingService;
import com.stayease.service.CustomUserDetailsService.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Booking management API")
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get booking by code")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByCode(@PathVariable String code) {
        BookingResponse booking = bookingService.getBookingByCode(code);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/my-bookings")
    @Operation(summary = "Get my bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BookingResponse> bookings = bookingService.getGuestBookings(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/host-bookings")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get host bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> getHostBookings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BookingResponse> bookings = bookingService.getHostBookings(currentUser.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUpcomingBookings(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<BookingResponse> bookings = bookingService.getUpcomingBookings(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/check-availability")
    @Operation(summary = "Check availability")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkAvailability(
            @RequestParam Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        boolean available = bookingService.isPropertyAvailable(propertyId, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }
    
    @GetMapping("/booked-dates/{propertyId}")
    @Operation(summary = "Get booked dates for property")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getBookedDates(@PathVariable Long propertyId) {
        List<LocalDate> bookedDates = bookingService.getBookedDates(propertyId);
        return ResponseEntity.ok(ApiResponse.success(bookedDates));
    }

    @PostMapping
    @Operation(summary = "Create booking")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created", booking));
    }
    
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Confirm booking")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed", booking));
    }
    
    @PutMapping("/{id}/payment")
    @Operation(summary = "Process payment for booking")
    public ResponseEntity<ApiResponse<BookingResponse>> processPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {
        BookingResponse booking = bookingService.processPayment(id, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", booking));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking (Guest or Host can cancel/reject)")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        BookingResponse booking = bookingService.cancelBooking(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", booking));
    }

    @GetMapping("/host-stats")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get host booking statistics")
    public ResponseEntity<ApiResponse<BookingStatsResponse>> getHostBookingStats(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        BookingStatsResponse stats = bookingService.getHostBookingStats(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/host-calendar")
    @PreAuthorize("hasRole('HOST')")
    @Operation(summary = "Get host booking calendar for a specific month")
    public ResponseEntity<ApiResponse<BookingCalendarResponse>> getHostBookingCalendar(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        // Use current date if not specified
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        BookingCalendarResponse calendar = bookingService.getHostBookingCalendar(currentUser.getId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(calendar));
    }
}

package com.stayease.service;

import com.stayease.dto.request.CreateBookingRequest;
import com.stayease.dto.request.SubmitTransferProofRequest;
import com.stayease.dto.response.BookingResponse;
import com.stayease.dto.response.BookingStatsResponse;
import com.stayease.dto.response.BookingCalendarResponse;
import com.stayease.dto.response.CalendarBookingResponse;
import com.stayease.dto.response.PageResponse;
import com.stayease.enums.BookingStatus;
import com.stayease.enums.PaymentStatus;
import com.stayease.exception.BadRequestException;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.exception.UnauthorizedException;
import com.stayease.model.Booking;
import com.stayease.model.Property;
import com.stayease.model.User;
import com.stayease.repository.BookingRepository;
import com.stayease.repository.PropertyRepository;
import com.stayease.utils.BookingMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final SettlementService settlementService;
    private final BookingSettlementService bookingSettlementService;
    
    public BookingService(BookingRepository bookingRepository,
                          PropertyRepository propertyRepository,
                          UserService userService,
                          BookingMapper bookingMapper,
                          SettlementService settlementService,
                          BookingSettlementService bookingSettlementService) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
        this.settlementService = settlementService;
        this.bookingSettlementService = bookingSettlementService;
    }
    
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return bookingMapper.toResponse(booking);
    }
    
    public BookingResponse getBookingByCode(String code) {
        Booking booking = bookingRepository.findByBookingCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "code", code));
        return bookingMapper.toResponse(booking);
    }
    
    public PageResponse<BookingResponse> getGuestBookings(Long guestId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage = bookingRepository.findByGuestIdOrderByCreatedAtDesc(guestId, pageable);
        
        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(bookingPage, content);
    }
    
    public PageResponse<BookingResponse> getHostBookings(Long hostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage = bookingRepository.findByHostId(hostId, pageable);
        
        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
        
        return PageResponse.from(bookingPage, content);
    }
    
    public List<BookingResponse> getUpcomingBookings(Long guestId) {
        return bookingRepository.findUpcomingBookings(guestId, LocalDate.now()).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", request.getPropertyId()));
        
        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) || 
            request.getCheckOutDate().isEqual(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getPropertyId(), request.getCheckInDate(), request.getCheckOutDate());
        
        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Property is not available for the selected dates");
        }
        
        if (request.getNumGuests() > property.getMaxGuests()) {
            throw new BadRequestException("Number of guests exceeds maximum allowed");
        }
        
        int numNights = (int) ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal subtotal = property.getPricePerNight().multiply(BigDecimal.valueOf(numNights));
        BigDecimal cleaningFee = property.getCleaningFee() != null ? property.getCleaningFee() : BigDecimal.ZERO;
        BigDecimal serviceFee = property.getServiceFee() != null ? property.getServiceFee() : BigDecimal.ZERO;
        BigDecimal totalPrice = subtotal.add(cleaningFee).add(serviceFee);
        
        String bookingCode = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numGuests(request.getNumGuests())
                .numAdults(request.getNumAdults())
                .numChildren(request.getNumChildren())
                .numInfants(request.getNumInfants())
                .pricePerNight(property.getPricePerNight())
                .numNights(numNights)
                .subtotal(subtotal)
                .cleaningFee(cleaningFee)
                .serviceFee(serviceFee)
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING) // Always start with PENDING so host can review
                .paymentStatus(PaymentStatus.PENDING)
                .specialRequests(request.getSpecialRequests())
                .guestMessage(request.getGuestMessage())
                .guest(currentUser)
                .property(property)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }
    
    @Transactional
    public BookingResponse processPayment(Long id, String paymentMethod) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        User currentUser = userService.getCurrentUser();
        if (!booking.getGuest().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the guest can process payment for this booking");
        }

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("This booking has already been paid");
        }

        // Simulate payment processing (in production, integrate with real payment gateway)
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentMethod(paymentMethod);

        // If instant book, payment automatically confirms the booking
        if (Boolean.TRUE.equals(booking.getProperty().getIsInstantBook())) {
            booking.setStatus(BookingStatus.PENDING);
        }

        Booking savedBooking = bookingRepository.save(booking);
        bookingSettlementService.createOrRecalculateForBooking(savedBooking.getId(), LocalDateTime.now());
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        User currentUser = userService.getCurrentUser();
        if (!booking.getProperty().getHost().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the host can confirm this booking");
        }
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be confirmed");
        }
        
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            // Bank transfer (QR) flow: host can confirm once guest submitted proof.
            if (booking.getTransferProofImageUrl() == null || booking.getTransferProofImageUrl().isBlank()) {
                throw new BadRequestException("Payment must be completed before confirmation");
            }
            // Mark as paid when host confirms proof.
            booking.setPaymentStatus(PaymentStatus.PAID);
            if (booking.getPaymentMethod() == null || booking.getPaymentMethod().isBlank()) {
                booking.setPaymentMethod("QR_CODE");
            }
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        if (savedBooking.getPaymentStatus() == PaymentStatus.PAID) {
            bookingSettlementService.createOrRecalculateForBooking(savedBooking.getId(), LocalDateTime.now());
        }
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse submitTransferProof(Long bookingId, SubmitTransferProofRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        User currentUser = userService.getCurrentUser();
        if (!booking.getGuest().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the guest can submit transfer proof for this booking");
        }

        if (request.getTransferProofImageUrl() == null || request.getTransferProofImageUrl().isBlank()) {
            throw new BadRequestException("transferProofImageUrl is required");
        }

        booking.setTransferProofImageUrl(request.getTransferProofImageUrl());
        booking.setTransferReference(request.getTransferReference());
        booking.setPaymentMethod("QR_CODE");

        // Keep paymentStatus as PENDING; host will verify proof and then confirm.
        // (Optional future: add a separate proof status.)

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        User currentUser = userService.getCurrentUser();
        boolean isGuest = booking.getGuest().getId().equals(currentUser.getId());
        boolean isHost = booking.getProperty().getHost().getId().equals(currentUser.getId());
        
        if (!isGuest && !isHost) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        // Policy: Guest can cancel PENDING/CONFIRMED if check-in is in the future (server time).
        // Host can cancel/reject (still cannot cancel COMPLETED).
        LocalDateTime now = LocalDateTime.now();

        // Compute check-in date time based on property check-in time (fallback 14:00 if missing)
        LocalTime checkInTime = parseLocalTimeOrDefault(booking.getProperty().getCheckInTime(), LocalTime.of(14, 0));
        LocalDateTime checkInDateTime = booking.getCheckInDate().atTime(checkInTime);

        if (isGuest) {
            if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new BadRequestException("Guests can only cancel PENDING or CONFIRMED bookings");
            }
            if (!checkInDateTime.isAfter(now)) {
                throw new BadRequestException("Bookings cannot be cancelled after check-in time");
            }
        }
        
        // Host can reject PENDING bookings or cancel CONFIRMED bookings
        // Guest can only cancel their own bookings (any status except COMPLETED)
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Completed bookings cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledBy(isGuest ? "USER" : "HOST");

        // Refund: use totalPaid = booking.totalPrice (as requested)
        // - Host cancellation: always 100%
        // - Guest cancellation: depends on property.cancellationPolicy (Option A)
        BigDecimal totalPaid = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
        BigDecimal refundAmount;
        if (isHost) {
            refundAmount = totalPaid;
        } else {
            refundAmount = calculateGuestRefundAmount(
                    totalPaid,
                    booking.getProperty().getCancellationPolicy(),
                    now,
                    checkInDateTime
            );
        }
        booking.setRefundAmount(refundAmount);
        
        // If host is cancelling/rejecting, store response
        if (isHost && reason != null) {
            booking.setHostResponse(reason);
        }

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    private LocalTime parseLocalTimeOrDefault(String time, LocalTime defaultValue) {
        if (time == null || time.isBlank()) return defaultValue;
        try {
            // Support formats like "14:00" or "14:00:00"
            return LocalTime.parse(time.trim().length() == 5 ? time.trim() + ":00" : time.trim());
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    /**
     * Option A cancellation policies (string-based) using improved model:
     * - Flexible: 100% if >= 24h, 50% if >= 5 days, else 0%
     * - Moderate: 100% if >= 5 days, 50% if >= 24h, else 0%
     * - Strict: 0%
     *
     * We match by keyword in the stored policy text to tolerate current DB values.
     */
    private BigDecimal calculateGuestRefundAmount(
            BigDecimal totalPaid,
            String cancellationPolicyText,
            LocalDateTime now,
            LocalDateTime checkInDateTime
    ) {
        if (totalPaid == null) return BigDecimal.ZERO;
        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        long hoursBefore = ChronoUnit.HOURS.between(now, checkInDateTime);
        long daysBefore = ChronoUnit.DAYS.between(now, checkInDateTime);

        String policy = cancellationPolicyText == null ? "" : cancellationPolicyText.toLowerCase();

        // STRICT
        if (policy.contains("nghiem") || policy.contains("nghiêm") || policy.contains("strict")) {
            return BigDecimal.ZERO;
        }

        // MODERATE
        if (policy.contains("trung") || policy.contains("moderate")) {
            if (daysBefore >= 3) return totalPaid;
            if (hoursBefore >= 24) return percent(totalPaid, 50);
            return BigDecimal.ZERO;
        }

        // FLEXIBLE (default)
        if (policy.contains("linh") || policy.contains("flexible") || policy.isBlank()) {
            if (hoursBefore >= 24) return totalPaid;
            if (daysBefore >= 3) return percent(totalPaid, 50);
            return BigDecimal.ZERO;
        }

        // Fallback: treat unknown policy as strict to be safe.
        return BigDecimal.ZERO;
    }

    private BigDecimal percent(BigDecimal amount, int percent) {
        return amount
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    public boolean isPropertyAvailable(Long propertyId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> conflicts = bookingRepository.findConflictingBookings(propertyId, checkIn, checkOut);
        return conflicts.isEmpty();
    }

    public List<LocalDate> getBookedDates(Long propertyId) {
        List<Booking> bookings = bookingRepository.findConflictingBookings(
                propertyId,
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );

        return bookings.stream()
                .flatMap(booking -> {
                    List<LocalDate> dates = new java.util.ArrayList<>();
                    LocalDate date = booking.getCheckInDate();
                    while (!date.isAfter(booking.getCheckOutDate())) {
                        dates.add(date);
                        date = date.plusDays(1);
                    }
                    return dates.stream();
                })
                .distinct()
                .collect(Collectors.toList());
    }

    public BookingStatsResponse getHostBookingStats(Long hostId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfPreviousMonth = startOfMonth.minusMonths(1);
        LocalDate endOfPreviousMonth = startOfMonth.minusDays(1);

        // Pending bookings count (paid but not confirmed) - unchanged
        Long pendingCount = bookingRepository.countByHostIdAndStatusAndPaymentStatus(
                hostId, BookingStatus.PENDING, PaymentStatus.PAID);

        // Confirmed bookings this month - unchanged
        Long confirmedThisMonth = bookingRepository.countByHostIdAndStatusAndCreatedAtBetween(
                hostId, BookingStatus.CONFIRMED, startOfMonth.atStartOfDay(), now.atTime(23, 59, 59));

        // Confirmed bookings previous month - unchanged
        Long previousMonthConfirmed = bookingRepository.countByHostIdAndStatusAndCreatedAtBetween(
                hostId, BookingStatus.CONFIRMED, startOfPreviousMonth.atStartOfDay(), endOfPreviousMonth.atTime(23, 59, 59));

        // New business rule: Host does NOT receive full booking total directly.
        // We show revenues using host payout snapshot if available.
        BigDecimal expectedRevenue = BigDecimal.ZERO;
        BigDecimal upcomingRevenue = BigDecimal.ZERO;

        // For MVP we compute from recent host bookings. (Can be optimized with DB sum queries later.)
        List<Booking> bookings = bookingRepository.findByHostId(hostId, PageRequest.of(0, 10_000)).getContent();
        LocalDateTime nowDt = LocalDateTime.now();

        for (Booking b : bookings) {
            if (b.getPaymentStatus() != PaymentStatus.PAID) continue;
            if (b.getStatus() == BookingStatus.CANCELLED) continue;
            if (b.getHostPayoutAmountVnd() == null) continue;

            // expectedRevenue: sum of PAID bookings payout snapshot (money host should receive in total)
            expectedRevenue = expectedRevenue.add(b.getHostPayoutAmountVnd());

            // upcomingRevenue: sum of payout that is already eligible (due) by settlement rules
            LocalDateTime dueAt = settlementService.computeDueAt(b);
            if (dueAt != null && !nowDt.isBefore(dueAt)) {
                upcomingRevenue = upcomingRevenue.add(b.getHostPayoutAmountVnd());
            }
        }

        return BookingStatsResponse.builder()
                .pendingCount(pendingCount)
                .confirmedThisMonth(confirmedThisMonth)
                .previousMonthConfirmed(previousMonthConfirmed)
                .expectedRevenue(expectedRevenue)
                .upcomingRevenue(upcomingRevenue)
                .build();
    }

    public BookingCalendarResponse getHostBookingCalendar(Long hostId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Booking> bookings = bookingRepository.findByHostIdAndDateRange(hostId, startDate, endDate);

        List<CalendarBookingResponse> calendarBookings = bookings.stream()
                .flatMap(booking -> {
                    LocalDate checkIn = booking.getCheckInDate();
                    LocalDate checkOut = booking.getCheckOutDate();

                    return checkIn.datesUntil(checkOut)
                            .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                            .map(date -> {
                                String type = "guest";
                                if (date.equals(checkIn) && booking.getStatus() == BookingStatus.CONFIRMED) {
                                    type = "checked-in";
                                }

                                return CalendarBookingResponse.builder()
                                        .date(date)
                                        .type(type)
                                        .guestName(booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName())
                                        .bookingCode(booking.getBookingCode())
                                        .bookingId(booking.getId())
                                        .status(booking.getStatus().toString())
                                        .build();
                            });
                })
                .collect(Collectors.toList());

        String monthName = startDate.getMonth().toString();

        return BookingCalendarResponse.builder()
                .month(month)
                .year(year)
                .monthName(monthName)
                .bookings(calendarBookings)
                .build();
    }
}

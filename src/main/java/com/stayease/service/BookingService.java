package com.stayease.service;

import com.stayease.controller.request.CreateBookingRequest;
import com.stayease.controller.response.BookingResponse;
import com.stayease.controller.response.PageResponse;
import com.stayease.exception.BadRequestException;
import com.stayease.exception.ResourceNotFoundException;
import com.stayease.exception.UnauthorizedException;
import com.stayease.model.Booking;
import com.stayease.model.Booking.BookingStatus;
import com.stayease.model.Booking.PaymentStatus;
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
import java.time.LocalDate;
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
    
    public BookingService(BookingRepository bookingRepository,
                          PropertyRepository propertyRepository,
                          UserService userService,
                          BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
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
                .status(property.getIsInstantBook() ? BookingStatus.CONFIRMED : BookingStatus.PENDING)
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
        
        booking.setStatus(BookingStatus.CONFIRMED);
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
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setCancelledBy(isGuest ? "GUEST" : "HOST");
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }
    
    public boolean isPropertyAvailable(Long propertyId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> conflicts = bookingRepository.findConflictingBookings(propertyId, checkIn, checkOut);
        return conflicts.isEmpty();
    }
}


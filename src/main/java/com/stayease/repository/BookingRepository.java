package com.stayease.repository;

import com.stayease.model.Booking;
import com.stayease.model.Booking.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingCode(String bookingCode);
    
    Page<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.property.host.id = :hostId ORDER BY b.createdAt DESC")
    Page<Booking> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findConflictingBookings(@Param("propertyId") Long propertyId,
                                          @Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT b FROM Booking b WHERE b.guest.id = :guestId AND b.checkInDate > :today " +
           "AND b.status IN ('PENDING', 'CONFIRMED') ORDER BY b.checkInDate ASC")
    List<Booking> findUpcomingBookings(@Param("guestId") Long guestId, @Param("today") LocalDate today);
    
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.property.host.id = :hostId " +
           "AND b.status = 'COMPLETED' AND b.createdAt >= :startDate")
    BigDecimal getTotalRevenueByHost(@Param("hostId") Long hostId, @Param("startDate") LocalDate startDate);
}


package com.stayease.repository;

import com.stayease.enums.BookingSettlementStatus;
import com.stayease.model.BookingSettlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingSettlementRepository extends JpaRepository<BookingSettlement, Long> {

    Optional<BookingSettlement> findByBookingId(Long bookingId);

    @Query("SELECT bs FROM BookingSettlement bs WHERE bs.booking.property.host.id = :hostId")
    Page<BookingSettlement> findByHostId(@Param("hostId") Long hostId, Pageable pageable);

    @Query("SELECT bs FROM BookingSettlement bs WHERE bs.booking.property.host.id = :hostId AND bs.status = :status")
    Page<BookingSettlement> findByHostIdAndStatus(@Param("hostId") Long hostId,
                                                  @Param("status") BookingSettlementStatus status,
                                                  Pageable pageable);

    Page<BookingSettlement> findByStatus(BookingSettlementStatus status, Pageable pageable);

    List<BookingSettlement> findByBookingIdIn(List<Long> bookingIds);

    List<BookingSettlement> findByPayoutId(Long payoutId);

    List<BookingSettlement> findByBookingPropertyHostIdAndStatus(Long hostId, BookingSettlementStatus status);
}

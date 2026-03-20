package com.stayease.repository;

import com.stayease.enums.RefundRequestStatus;
import com.stayease.model.RefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    Optional<RefundRequest> findByBookingId(Long bookingId);

    Page<RefundRequest> findByGuestIdOrderByCreatedAtDesc(Long guestId, Pageable pageable);

    Page<RefundRequest> findByStatusOrderByCreatedAtDesc(RefundRequestStatus status, Pageable pageable);

    Page<RefundRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}


package com.stayease.repository;

import com.stayease.enums.HostPayoutStatus;
import com.stayease.model.HostPayoutItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HostPayoutItemRepository extends JpaRepository<HostPayoutItem, Long> {

    Optional<HostPayoutItem> findByBookingId(Long bookingId);

    List<HostPayoutItem> findByPayoutId(Long payoutId);

    List<HostPayoutItem> findByPayoutIdAndStatus(Long payoutId, HostPayoutStatus status);
}


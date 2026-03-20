package com.stayease.scheduler;

import com.stayease.model.Booking;
import com.stayease.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Auto-cancels abandoned QR checkout draft bookings.
 *
 * Why:
 * - Frontend best-effort cancellation can fail (tab close, refresh, network).
 * - This scheduled job ensures stale draft bookings don't accumulate.
 *
 * Current heuristic (no dedicated DRAFT status yet):
 * - status = PENDING
 * - paymentStatus = PENDING
 * - paymentMethod is null or QR_CODE
 * - transferProofImageUrl is null/blank
 * - createdAt older than a cutoff
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingDraftCleanupScheduler {

    private final BookingRepository bookingRepository;

    /**
     * Draft TTL in minutes.
     * Defaults to 15 minutes.
     */
    @Value("${stayease.bookingDraftCleanup.ttlMinutes:15}")
    private int ttlMinutes;

    /**
     * Runs every 5 minutes.
     * NOTE: choose a conservative TTL to avoid cancelling real pending bookings.
     * You can tune this after introducing a dedicated draft status.
     */
    @Scheduled(fixedDelayString = "${stayease.bookingDraftCleanup.fixedDelayMs:300000}")
    @Transactional
    public void cleanupAbandonedQrDrafts() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(ttlMinutes);

        List<Booking> toCancel = bookingRepository.findAbandonedQrDrafts(cutoff);
        if (toCancel.isEmpty()) {
            return;
        }

        for (Booking b : toCancel) {
            // guard: if something changed between query and now
            if (b.getTransferProofImageUrl() != null && !b.getTransferProofImageUrl().isBlank()) {
                continue;
            }
            b.setStatus(com.stayease.enums.BookingStatus.CANCELLED);
            b.setCancelledBy("SYSTEM");
            b.setCancellationReason("Auto-cancel abandoned QR checkout draft (no transfer proof)");
        }

        bookingRepository.saveAll(toCancel);
        log.info("Auto-cancelled {} abandoned QR draft booking(s) older than {} minute(s)", toCancel.size(), ttlMinutes);
    }
}



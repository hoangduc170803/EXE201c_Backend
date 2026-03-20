package com.stayease.model;

import com.stayease.enums.HostPayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "host_payout_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_payout_item_booking", columnNames = {"booking_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostPayoutItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id")
    private HostPayout payout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "amount_vnd", precision = 18, scale = 0, nullable = false)
    private BigDecimal amountVnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private HostPayoutStatus status = HostPayoutStatus.DUE;
}


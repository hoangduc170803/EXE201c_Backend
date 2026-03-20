package com.stayease.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refund_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundAccount extends BaseEntity {

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false, length = 150)
    private String accountHolder;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


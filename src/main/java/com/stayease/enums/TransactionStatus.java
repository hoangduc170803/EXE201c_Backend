package com.stayease.enums;

public enum TransactionStatus {
    PENDING,          // Chờ xử lý
    PROCESSING,       // Đang xử lý
    COMPLETED,        // Hoàn thành
    FAILED,          // Thất bại
    CANCELLED,       // Đã hủy
    REFUNDED         // Đã hoàn tiền
}


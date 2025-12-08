package com.example.test_ecommerce.ecommerce.enums;

public enum OrderItemStatus {
    PENDING, // Order created, waiting for payment
    PAID, // Payment completed, ready to ship
    SHIPPED, // Shipped to customer
    DELIVERED, // Delivered to customer
    CANCELED, // Canceled by customer/seller
    RETURNED, // Returned by customer
    REFUNDED // Refunded
}

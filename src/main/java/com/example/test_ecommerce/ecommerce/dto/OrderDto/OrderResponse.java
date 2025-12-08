package com.example.test_ecommerce.ecommerce.dto.OrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.test_ecommerce.ecommerce.dto.UserDto.AddressResponse;
import com.example.test_ecommerce.ecommerce.entitiy.Order;
import com.example.test_ecommerce.ecommerce.enums.OrderStatus;
import com.example.test_ecommerce.ecommerce.enums.PaymentMethod;
import com.example.test_ecommerce.ecommerce.enums.PaymentStatus;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private BigDecimal finalAmount;
    private AddressResponse shippingAddress;
    private AddressResponse billingAddress;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public void fromOrder(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.orderStatus = order.getOrderStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();
        this.totalAmount = order.getTotalAmount();
        this.totalDiscount = order.getTotalDiscount();
        this.finalAmount = order.getFinalAmount();

        // Convert shipping address
        AddressResponse shippingAddr = new AddressResponse();
        shippingAddr.fromEntity(order.getShippingAddress());
        this.shippingAddress = shippingAddr;

        // Convert billing address if present
        if (order.getBillingAddress() != null) {
            AddressResponse billingAddr = new AddressResponse();
            billingAddr.fromEntity(order.getBillingAddress());
            this.billingAddress = billingAddr;
        }

        // Convert order items
        this.items = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.fromOrderItem(item);
                    return itemResponse;
                })
                .collect(Collectors.toList());

        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.paidAt = order.getPaidAt();
        this.shippedAt = order.getShippedAt();
        this.deliveredAt = order.getDeliveredAt();
    }
}

package com.example.test_ecommerce.ecommerce.dto.OrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.test_ecommerce.ecommerce.entitiy.OrderItem;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Long sellerId;
    private String sellerBusinessName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal discountAtPurchase;
    private BigDecimal totalPrice;
    private OrderItemStatus itemStatus;
    private LocalDateTime createdAt;

    public void fromOrderItem(OrderItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.productImage = item.getProduct().getImage();
        this.sellerId = item.getSellerProfile().getId();
        this.sellerBusinessName = item.getSellerProfile().getBusinessName();
        this.quantity = item.getQuantity();
        this.priceAtPurchase = item.getPriceAtPurchase();
        this.discountAtPurchase = item.getDiscountAtPurchase();
        this.totalPrice = item.getTotalPrice();
        this.itemStatus = item.getItemStatus();
        this.createdAt = item.getCreatedAt();
    }
}

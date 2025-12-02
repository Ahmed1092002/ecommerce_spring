package com.example.test_ecommerce.ecommerce.dto.CartItemDto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDtoList {
    private Long cartItemId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;

}

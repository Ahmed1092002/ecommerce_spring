package com.example.test_ecommerce.ecommerce.dto.CartItemDto;

import lombok.Data;

@Data
public class CartItemDtoList {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private Double totalPrice;


}

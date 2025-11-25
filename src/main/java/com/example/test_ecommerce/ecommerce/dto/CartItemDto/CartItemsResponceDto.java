package com.example.test_ecommerce.ecommerce.dto.CartItemDto;


import java.util.List;

import lombok.Data;

@Data
public class CartItemsResponceDto {
    private Double totalCartPrice;
    private Integer totalItems;
    private Integer totalQuantity;
    private Double totalDiscount;
    private List<CartItemDtoList> cartItems;

}

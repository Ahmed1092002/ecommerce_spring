package com.example.test_ecommerce.ecommerce.dto.CartItemDto;


import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartItemsResponceDto {
    private BigDecimal totalCartPrice;
    private Integer totalItems;
    private Integer totalQuantity;
    private BigDecimal totalDiscount;
    private List<CartItemDtoList> cartItems;

}

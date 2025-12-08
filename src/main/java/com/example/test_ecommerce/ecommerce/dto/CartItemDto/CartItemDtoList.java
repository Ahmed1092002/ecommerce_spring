package com.example.test_ecommerce.ecommerce.dto.CartItemDto;

import java.math.BigDecimal;

import com.example.test_ecommerce.ecommerce.entitiy.CartItem;

import lombok.Data;

@Data
public class CartItemDtoList {
    private Long cartItemId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String image;

    public void fromCartItem(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.name = cartItem.getProduct().getName();
        this.price = cartItem.getPrice();
        this.quantity = cartItem.getQuantity();
        this.totalPrice = cartItem.getTotalPrice();
        this.image = cartItem.getProduct().getImage();
    }

}

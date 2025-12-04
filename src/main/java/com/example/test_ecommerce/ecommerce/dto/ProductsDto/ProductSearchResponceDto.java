package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import com.example.test_ecommerce.ecommerce.entitiy.Products;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductSearchResponceDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal discount;
    private BigDecimal finalPrice;

    public void fromEntity(Products product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.discount = product.getDiscount();
        this.finalPrice = product.getFinalPrice();
    }

}

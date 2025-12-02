package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

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
}

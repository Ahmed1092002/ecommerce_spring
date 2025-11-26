package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import lombok.Data;

@Data
public class ProductSearchResponceDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Double discount;
    private Double finalPrice;
}

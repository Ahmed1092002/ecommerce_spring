package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import lombok.Data;

@Data
public class ProductSearchResponceDto {
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private Integer quantity;
    private Double discount;
    private Double finalPrice;
}

package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import org.springframework.security.access.method.P;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductCreateDto {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "Price is mandatory")
    private Double price;
    @NotBlank(message = "Rating is mandatory")
    private Double rating;
    @NotBlank(message = "Quantity is mandatory")
    private Integer quantity;

}

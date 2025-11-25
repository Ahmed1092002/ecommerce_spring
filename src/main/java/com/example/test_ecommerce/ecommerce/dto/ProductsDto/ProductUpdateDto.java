package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @NotBlank(message = "Id is mandatory")
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "Price is mandatory")
    private Double price;
    @NotBlank(message = "Rating is mandatory")
    @Size(min = 0, max = 5, message = "Rating must be between 0 and 5")
    private Double rating;
    @NotBlank(message = "Quantity is mandatory")
    @Size(min = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    @NotBlank(message = "Discount is mandatory")
    @Size(min = 0, max = 100, message = "Discount must be between 0 and 100")
    private Double discount;

}

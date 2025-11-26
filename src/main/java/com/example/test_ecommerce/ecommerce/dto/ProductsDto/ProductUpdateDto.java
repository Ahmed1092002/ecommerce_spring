package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @NotNull(message = "Id is mandatory")
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", message = "Price must be at least 0")
    private Double price;

    @NotNull(message = "Quantity is mandatory")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    @NotNull(message = "Discount is mandatory")
    @DecimalMin(value = "0.0", message = "Discount must be at least 0")
    @DecimalMax(value = "100.0", message = "Discount must be at most 100")
    private Double discount;

}

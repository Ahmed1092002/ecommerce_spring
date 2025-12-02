package com.example.test_ecommerce.ecommerce.dto.CartItemDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDto {
    @NotNull(message = "Product ID is mandatory")
    private Long productId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}

package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @NotNull(message = "Image is mandatory")
    private String image;
    @NotNull(message = "Id is mandatory")
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", message = "Price must be at least 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is mandatory")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    @NotNull(message = "Discount is mandatory")
    @DecimalMin(value = "0.0", message = "Discount must be at least 0")
    @DecimalMax(value = "100.0", message = "Discount must be at most 100")
    private BigDecimal discount;

    public Products toProduct() {
        Products product = new Products();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setQuantity(this.quantity);
        product.setDiscount(this.discount);
        product.setImage(this.image);
        return product;
    }

}

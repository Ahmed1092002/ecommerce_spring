package com.example.test_ecommerce.ecommerce.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SellerProfileDto {

    @NotBlank(message = "Business name is required")
    private String businessName;
}

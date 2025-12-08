package com.example.test_ecommerce.ecommerce.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerProfileDto {

    @NotBlank(message = "Name is required")
    private String name;
}

package com.example.test_ecommerce.ecommerce.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String username;

    private String email;

    private String userType;

    private String token;
}

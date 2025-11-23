package com.example.test_ecommerce.ecommerce.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Size(min = 3, max = 50)
    @NotBlank(message = "Username cannot be blank")
    private String login;
    @Size(min = 8, max = 100)
    @NotBlank(message = "Password cannot be blank")
    private String password;

}

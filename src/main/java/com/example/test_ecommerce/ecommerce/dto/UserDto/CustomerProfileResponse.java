package com.example.test_ecommerce.ecommerce.dto.UserDto;

import com.example.test_ecommerce.ecommerce.entitiy.CustomerProfile;
import com.example.test_ecommerce.ecommerce.enums.UserType;

import lombok.Data;

@Data
public class CustomerProfileResponse {

    private String name;
    private String email;
    private UserType userType;

    public void fromEntity(CustomerProfile customerProfile) {
        this.name = customerProfile.getName();
        this.email = customerProfile.getUser().getEmail();
        this.userType = customerProfile.getUser().getUserType();
    }
}

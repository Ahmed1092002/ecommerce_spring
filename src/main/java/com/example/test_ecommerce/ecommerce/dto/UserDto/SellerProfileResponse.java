package com.example.test_ecommerce.ecommerce.dto.UserDto;

import com.example.test_ecommerce.ecommerce.entitiy.SellerProfile;
import com.example.test_ecommerce.ecommerce.enums.UserType;

import lombok.Data;

@Data
public class SellerProfileResponse {

    private String businessName;
    private String email;
    private UserType userType;

    public void fromEntity(SellerProfile sellerProfile) {
        this.businessName = sellerProfile.getBusinessName();
        this.email = sellerProfile.getUser().getEmail();
        this.userType = sellerProfile.getUser().getUserType();
    }
}

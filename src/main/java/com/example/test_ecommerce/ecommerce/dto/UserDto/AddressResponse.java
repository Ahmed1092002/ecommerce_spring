package com.example.test_ecommerce.ecommerce.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import com.example.test_ecommerce.ecommerce.enums.AddressType;
import com.example.test_ecommerce.ecommerce.entitiy.Address;
import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String label;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Boolean isDefault;
    private AddressType type;

    public void fromEntity(Address address) {
        this.id = address.getId();
        this.label = address.getLabel();
        this.phone = address.getPhone();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
        this.country = address.getCountry();
        this.isDefault = address.getIsDefault();
        this.type = address.getType();
    }
}

package com.example.test_ecommerce.ecommerce.dto.UserDto;

import lombok.Data;
import com.example.test_ecommerce.ecommerce.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.test_ecommerce.ecommerce.entitiy.Address;

@Data
public class AddressRequestDto {
    @NotBlank(message = "Label is required")
    private String label;
    @NotBlank(message = "Phone is required")
    private String phone;
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    @NotBlank(message = "Country is required")
    private String country;
    @NotNull(message = "Is default is required")
    private Boolean isDefault;
    @NotNull(message = "Type is required")
    private AddressType type;

    public Address toAddress() {
        Address address = new Address();
        address.setLabel(this.label);
        address.setPhone(this.phone);
        address.setStreet(this.street);
        address.setCity(this.city);
        address.setState(this.state);
        address.setZipCode(this.zipCode);
        address.setCountry(this.country);
        address.setIsDefault(this.isDefault);
        address.setType(this.type);
        return address;
    }

}

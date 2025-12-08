package com.example.test_ecommerce.ecommerce.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.UserDto.CustomerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.CustomerProfileResponse;
import com.example.test_ecommerce.ecommerce.entitiy.CustomerProfile;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.CustomerProfileRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final GetCurrentUser getCurrentUser;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository,
            GetCurrentUser getCurrentUser) {
        this.customerProfileRepository = customerProfileRepository;
        this.getCurrentUser = getCurrentUser;
    }

    /**
     * Get the current customer's profile
     */
    public CustomerProfileResponse getCustomerProfile() {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can access customer profiles.");
        }

        CustomerProfile customerProfile = customerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ValidationException("Customer profile not found."));

        CustomerProfileResponse response = new CustomerProfileResponse();
        response.fromEntity(customerProfile);
        return response;
    }

    /**
     * Update the current customer's profile
     */
    public CustomerProfileResponse updateCustomerProfile(CustomerProfileDto dto) {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can update customer profiles.");
        }

        CustomerProfile customerProfile = customerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ValidationException("Customer profile not found."));

        customerProfile.setName(dto.getName());
        customerProfileRepository.save(customerProfile);

        CustomerProfileResponse response = new CustomerProfileResponse();
        response.fromEntity(customerProfile);
        return response;
    }

    public Map<String, Object> createCustomerProfile(CustomerProfileDto dto) {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can create customer profiles.");
        }

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setName(dto.getName());
        customerProfile.setUser(user);
        customerProfileRepository.save(customerProfile);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Seller profile created successfully.");
        return response;
    }
}

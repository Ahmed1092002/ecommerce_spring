package com.example.test_ecommerce.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.UserDto.CustomerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.CustomerProfileResponse;
import com.example.test_ecommerce.ecommerce.services.CustomerProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/profile")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    public CustomerProfileController(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GetMapping
    public ResponseEntity<CustomerProfileResponse> getProfile() {
        return ResponseEntity.ok(customerProfileService.getCustomerProfile());
    }

    @PutMapping
    public ResponseEntity<CustomerProfileResponse> updateProfile(@Valid @RequestBody CustomerProfileDto dto) {
        return ResponseEntity.ok(customerProfileService.updateCustomerProfile(dto));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProfile(@Valid @RequestBody CustomerProfileDto dto) {
        return ResponseEntity.ok(customerProfileService.createCustomerProfile(dto));
    }

}

package com.example.test_ecommerce.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerStatsResponse;
import com.example.test_ecommerce.ecommerce.services.SellerProfileService;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/profile")
@PreAuthorize("hasRole('SELLER')")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    public SellerProfileController(SellerProfileService sellerProfileService) {
        this.sellerProfileService = sellerProfileService;
    }

    @GetMapping
    public ResponseEntity<SellerProfileResponse> getProfile() {
        return ResponseEntity.ok(sellerProfileService.getSellerProfile());
    }

    @PutMapping
    public ResponseEntity<SellerProfileResponse> updateProfile(@Valid @RequestBody SellerProfileDto dto) {
        return ResponseEntity.ok(sellerProfileService.updateSellerProfile(dto));
    }

    @GetMapping("/stats")
    public ResponseEntity<SellerStatsResponse> getStats() {
        return ResponseEntity.ok(sellerProfileService.getSellerStats());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProfile(@Valid @RequestBody SellerProfileDto dto) {
        return ResponseEntity.ok(sellerProfileService.createSellerProfile(dto));
    }
}

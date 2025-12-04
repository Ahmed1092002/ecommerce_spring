package com.example.test_ecommerce.ecommerce.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerStatsResponse;
import com.example.test_ecommerce.ecommerce.entitiy.SellerProfile;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.repository.SellerProfileRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;
    private final GetCurrentUser getCurrentUser;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository,
            ProductRepository productRepository,
            GetCurrentUser getCurrentUser) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.productRepository = productRepository;
        this.getCurrentUser = getCurrentUser;
    }

    /**
     * Get the current seller's profile
     */
    public SellerProfileResponse getSellerProfile() {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can access seller profiles.");
        }

        SellerProfile sellerProfile = sellerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ValidationException("Seller profile not found."));

        SellerProfileResponse response = new SellerProfileResponse();
        response.fromEntity(sellerProfile);
        return response;
    }

    /**
     * Update the current seller's profile
     */
    public SellerProfileResponse updateSellerProfile(SellerProfileDto dto) {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can update seller profiles.");
        }

        SellerProfile sellerProfile = sellerProfileRepository.findById(user.getId())
                .orElseThrow(() -> new ValidationException("Seller profile not found."));

        sellerProfile.setBusinessName(dto.getBusinessName());
        sellerProfileRepository.save(sellerProfile);

        SellerProfileResponse response = new SellerProfileResponse();
        response.fromEntity(sellerProfile);
        return response;
    }

    /**
     * Get seller statistics (product count)
     */
    public SellerStatsResponse getSellerStats() {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can access seller statistics.");
        }

        Long totalProducts = productRepository.countBySellerProfileId(user.getId());
        return new SellerStatsResponse(totalProducts);
    }

    public Map<String, Object> createSellerProfile(SellerProfileDto dto) {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can create seller profiles.");
        }

        SellerProfile sellerProfile = new SellerProfile();
        sellerProfile.setBusinessName(dto.getBusinessName());
        sellerProfile.setUser(user);
        sellerProfileRepository.save(sellerProfile);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Seller profile created successfully.");
        return response;
    }
}

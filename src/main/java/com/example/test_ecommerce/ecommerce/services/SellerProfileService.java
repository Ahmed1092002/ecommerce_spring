package com.example.test_ecommerce.ecommerce.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerProfileResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerStatsResponse;
import com.example.test_ecommerce.ecommerce.entitiy.SellerProfile;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.OrderItemRepository;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.repository.SellerProfileRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final GetCurrentUser getCurrentUser;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository,
            ProductRepository productRepository,
            OrderItemRepository orderItemRepository,
            GetCurrentUser getCurrentUser) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
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
     * Get seller statistics (product count, orders, revenue)
     */
    public SellerStatsResponse getSellerStats() {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can access seller statistics.");
        }

        Long sellerId = user.getId();

        // Total products
        Long totalProducts = productRepository.countBySellerProfileId(sellerId);

        // Total orders
        Long totalOrders = orderItemRepository.countDistinctOrdersBySeller(sellerId);

        // Orders by status
        Long pendingOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.PENDING);
        Long shippedOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.SHIPPED);
        Long deliveredOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.DELIVERED);

        // Revenue calculation - only for completed statuses
        List<OrderItemStatus> completedStatuses = Arrays.asList(
                OrderItemStatus.SHIPPED, OrderItemStatus.DELIVERED);
        BigDecimal totalRevenue = orderItemRepository.calculateTotalRevenueBySeller(
                sellerId, completedStatuses);

        // Average order value
        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (totalOrders != null && totalOrders > 0 && totalRevenue != null) {
            avgOrderValue = totalRevenue.divide(
                    BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }

        return SellerStatsResponse.builder()
                .totalProducts(totalProducts != null ? totalProducts : 0L)
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .pendingOrders(pendingOrders != null ? pendingOrders : 0L)
                .shippedOrders(shippedOrders != null ? shippedOrders : 0L)
                .deliveredOrders(deliveredOrders != null ? deliveredOrders : 0L)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .averageOrderValue(avgOrderValue)
                .build();
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

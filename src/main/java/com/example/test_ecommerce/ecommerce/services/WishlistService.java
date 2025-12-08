package com.example.test_ecommerce.ecommerce.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.repository.CustomerProfileRepository;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.repository.WishlistItemRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.entitiy.CustomerProfile;
import com.example.test_ecommerce.ecommerce.entitiy.WishlistItem;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.*;
import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;
import com.example.test_ecommerce.ecommerce.entitiy.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final GetCurrentUser getCurrentUser;
    private final ProductRepository productRepository;
    private final CustomerProfileRepository customerProfileRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository, GetCurrentUser getCurrentUser,
            ProductRepository productRepository, CustomerProfileRepository customerProfileRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.getCurrentUser = getCurrentUser;
        this.productRepository = productRepository;
        this.customerProfileRepository = customerProfileRepository;
    }

    public Map<String, Object> addToWishlist(Long productId) {
        Users currentUser = getCurrentUser.getCurrentUser();
        if (currentUser.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can add products to their wishlist.");
        }
        Long userId = currentUser.getId();
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found with id: " + productId));
        CustomerProfile customerProfile = customerProfileRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Customer profile not found with id: " + userId));

        if (wishlistItemRepository.existsByCustomerProfileAndProduct(customerProfile, product)) {
            throw new ValidationException("Product already exists in wishlist.");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setCustomerProfile(customerProfile);
        wishlistItem.setProduct(product);
        wishlistItem.setAddedAt(LocalDateTime.now());
        wishlistItemRepository.save(wishlistItem);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product added to wishlist successfully");
        return response;
    }

    public Map<String, Object> removeFromWishlist(Long productId) {
        Users currentUser = getCurrentUser.getCurrentUser();
        if (currentUser.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can remove products from their wishlist.");
        }
        Long userId = currentUser.getId();
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found with id: " + productId));
        CustomerProfile customerProfile = customerProfileRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Customer profile not found with id: " + userId));
        WishlistItem wishlistItem = wishlistItemRepository.findByCustomerProfileAndProduct(customerProfile, product)
                .orElseThrow(() -> new ValidationException("Product not found in wishlist."));
        wishlistItemRepository.delete(wishlistItem);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product removed from wishlist successfully");
        return response;
    }

    public GenericPageResponse<ProductSearchResponceDto> getWishlistItems(int page) {
        Users currentUser = getCurrentUser.getCurrentUser();
        if (currentUser.getUserType() != UserType.CUSTOMER) {
            throw new ValidationException("Only customers can view their wishlist.");
        }
        Long userId = currentUser.getId();
        Page<WishlistItem> wishlistItems = wishlistItemRepository.findByCustomerProfileId(userId,
                PageRequest.of(page, 10));
        GenericPageResponse<ProductSearchResponceDto> genericPageResponse = new GenericPageResponse<>();
        genericPageResponse.setPageNumber(wishlistItems.getNumber() + 1);
        genericPageResponse.setPageSize(wishlistItems.getSize());
        genericPageResponse.setTotalElements(wishlistItems.getTotalElements());
        genericPageResponse.setTotalPages(wishlistItems.getTotalPages());
        genericPageResponse.setData(wishlistItems.getContent().stream()
                .map(wishlistItem -> {
                    ProductSearchResponceDto productSearchResponceDto = new ProductSearchResponceDto();
                    productSearchResponceDto.fromEntity(wishlistItem.getProduct());

                    return productSearchResponceDto;
                })
                .collect(Collectors.toList()));
        return genericPageResponse;
    }
}

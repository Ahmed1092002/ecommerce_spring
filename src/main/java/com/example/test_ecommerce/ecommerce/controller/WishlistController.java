package com.example.test_ecommerce.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;
import com.example.test_ecommerce.ecommerce.services.WishlistService;

@RestController
@RequestMapping("/api/customer/wishlist")
@PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(productId));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.removeFromWishlist(productId));
    }

    @GetMapping("/items")
    public ResponseEntity<GenericPageResponse<ProductSearchResponceDto>> getWishlistItems(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(wishlistService.getWishlistItems(page - 1));
    }
}

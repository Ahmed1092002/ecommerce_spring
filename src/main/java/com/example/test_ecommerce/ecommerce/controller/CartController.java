package com.example.test_ecommerce.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemDto;
import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemsResponceDto;
import com.example.test_ecommerce.ecommerce.services.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/GetCart")
    public ResponseEntity<Map<String, CartItemsResponceDto>> getCart() {
        return ResponseEntity.ok(Map.of("cart", cartService.getCart()));
    }

    @PostMapping("/AddItem")
    public ResponseEntity<Map<String, String>> addItemToCart(@Valid @RequestBody CartItemDto cartItem) {
        return ResponseEntity.ok(Map.of("message", cartService.addItemToCart(cartItem)));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody Map<String, Integer> cartItem) {
        String message = cartService.editItemQuantity(cartItemId, cartItem.get("quantity"));
        return ResponseEntity.ok(Map.of("message", message));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Map<String, String>> removeItem(@PathVariable Long cartItemId) {
        String message = cartService.deleteItemFromCart(cartItemId);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearCart() {
        String message = cartService.clearCart();
        return ResponseEntity.ok(Map.of("message", message));
    }

}

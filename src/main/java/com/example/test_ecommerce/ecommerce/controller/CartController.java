package com.example.test_ecommerce.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemDto;
import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemsResponceDto;
import com.example.test_ecommerce.ecommerce.services.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/GetCart")
    public ResponseEntity<CartItemsResponceDto> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/AddItem")
    public ResponseEntity<String> addItemToCart(@Valid @RequestBody CartItemDto cartItem) {
        return ResponseEntity.ok(cartService.addItemToCart(cartItem));
    }

    @PatchMapping("/update/{cartItemId}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        String message = cartService.editItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long cartItemId) {
        String message = cartService.deleteItemFromCart(cartItemId);
        return ResponseEntity.ok(message);
    }
    
}

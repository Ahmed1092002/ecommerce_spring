package com.example.test_ecommerce.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/CreateProduct")
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductCreateDto productCreateDto) {
        return ResponseEntity.ok(productService.createProduct(productCreateDto));
    }

}

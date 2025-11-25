package com.example.test_ecommerce.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSerchResponceMapping;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductUpdateDto;
import com.example.test_ecommerce.ecommerce.services.ProductService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/GetProducts")
    public ResponseEntity<ProductSerchResponceMapping> getPublicCustomerProductsPaginationWithAscending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortedColumn,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = Double.MAX_VALUE + "") double maxPrice,
            @RequestParam(defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(productService.getPublicProductsPaginationWithAscending(
                page, size, sortedColumn, name, minPrice, maxPrice, ascending));

    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/GetProducts/seller")
    public ResponseEntity<ProductSerchResponceMapping> getSellerProductsPaginationWithAscending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortedColumn,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") double minPrice,
            @RequestParam(defaultValue = Double.MAX_VALUE + "") double maxPrice,
            @RequestParam(defaultValue = "true") boolean ascending) {
        return ResponseEntity.ok(productService.getProductsPaginationWithAscending(
                page, size, sortedColumn, name, minPrice, maxPrice, ascending));
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/DeleteProduct")
    public ResponseEntity<String> deleteProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(productService.DeleteProduct(productId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/updateproduct/{id}")
    public String updateProduct(@PathVariable String id, @Valid @RequestBody ProductUpdateDto entity) {

        return ResponseEntity.ok(productService.updateProduct(entity)).getBody();
    }

}

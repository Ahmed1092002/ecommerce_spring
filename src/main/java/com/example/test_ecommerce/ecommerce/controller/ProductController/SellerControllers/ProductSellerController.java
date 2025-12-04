package com.example.test_ecommerce.ecommerce.controller.ProductController.SellerControllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductUpdateDto;
import com.example.test_ecommerce.ecommerce.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/products")
@PreAuthorize("hasRole('SELLER')")
public class ProductSellerController {
    private final ProductService productService;

    public ProductSellerController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/CreateProduct")
    public ResponseEntity<Map<String, Object>> createProduct(
            @Valid @RequestBody ProductCreateDto productCreateDto) {
        return ResponseEntity.ok(productService.createProduct(productCreateDto));
    }

    @GetMapping("/GetProducts")
    public ResponseEntity<GenericPageResponse<ProductSearchResponceDto>> getSellerProductsPaginationWithAscending(
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

    @DeleteMapping("/DeleteProduct")
    public ResponseEntity<String> deleteProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(productService.DeleteProduct(productId));
    }

    @PutMapping("/updateproduct")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody ProductUpdateDto entity) {

        return ResponseEntity.ok(productService.updateProduct(entity));
    }
}

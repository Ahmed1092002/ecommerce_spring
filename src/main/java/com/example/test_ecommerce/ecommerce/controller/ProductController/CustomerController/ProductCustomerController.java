package com.example.test_ecommerce.ecommerce.controller.ProductController.CustomerController;

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

import com.example.test_ecommerce.ecommerce.services.ProductService;
import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/products")
@PreAuthorize("hasRole('CUSTOMER')")
public class ProductCustomerController {
    private final ProductService productService;

    public ProductCustomerController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/GetProducts")
    public ResponseEntity<GenericPageResponse<ProductSearchResponceDto>> getPublicCustomerProductsPaginationWithAscending(
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

}

package com.example.test_ecommerce.ecommerce.services;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final GetCurrentUser getCurrentUser;

    public ProductService(ProductRepository productRepository, GetCurrentUser getCurrentUser) {
        this.productRepository = productRepository;
        this.getCurrentUser = getCurrentUser;
    }

    public String CreateProduct(ProductCreateDto productCreateDto) {
        // You can access the current user details here
        Long userId = getCurrentUser.getCurrentUserId();
        Products product = new Products();
        product.setName(productCreateDto.getName());
        product.setDescription(productCreateDto.getDescription());
        product.setPrice(productCreateDto.getPrice());
        product.setRating(productCreateDto.getRating());
        product.setQuantity(productCreateDto.getQuantity());
        product.setUserId(userId);
        Products createdProduct = productRepository.save(product);

        return "Product created successfully by user ID: " + userId;

    }
}

package com.example.test_ecommerce.ecommerce.services;

import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.enums.UserType;
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
        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != UserType.SELLER) {
            throw new RuntimeException("Only Selles users can create products.");
        }
        // You can access the current user details here
        Products product = new Products();
        product.setName(productCreateDto.getName());
        product.setDescription(productCreateDto.getDescription());
        product.setPrice(productCreateDto.getPrice());
        product.setRating(productCreateDto.getRating());
        product.setQuantity(productCreateDto.getQuantity());
        product.setUser(getCurrentUser.getCurrentUser());
        product.setDiscount(productCreateDto.getDiscount());

        const double discountAmount = (productCreateDto.getDiscount() / 100) * productCreateDto.getPrice();
        product.setFinalPrice(productCreateDto.getPrice() - discountAmount);
        Products createdProduct = productRepository.save(product);

        return "Product created successfully : " + createdProduct.getName();

    }

    function

    double calculateFinalPrice(double price, double discount) {
        double discountAmount = (discount / 100) * price;
        return price - discountAmount;
    }
}

package com.example.test_ecommerce.ecommerce.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public String createProduct(ProductCreateDto productCreateDto) {
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

        double discountAmount = (productCreateDto.getDiscount() / 100) * productCreateDto.getPrice();
        product.setFinalPrice(productCreateDto.getPrice() - discountAmount);
        Products createdProduct = productRepository.save(product);

        return "Product created successfully : " + createdProduct.getName();

    }

    public double calculateFinalPrice(double price, double discount) {
        double discountAmount = (discount / 100) * price;
        return price - discountAmount;
    }

    // another example
    public Page<Products> getProductsPaginationWithAscending(int page, int size, String sortedColumn,
            String name,
            boolean ascending) {
        Long userId = getCurrentUser.getCurrentUserId();
        if (page == 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Sort sort = ascending ? Sort.by(sortedColumn).ascending() : Sort.by(sortedColumn).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return productRepository.findByUser_IdAndNameContainingIgnoreCase(userId, name, pageable);

    }
}

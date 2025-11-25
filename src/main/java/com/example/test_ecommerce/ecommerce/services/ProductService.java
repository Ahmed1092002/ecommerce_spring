package com.example.test_ecommerce.ecommerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSerchResponceMapping;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductUpdateDto;
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

    public ProductSerchResponceMapping getProductsPaginationWithAscending(int page, int size, String sortedColumn,
            String name,
            double minPrice,
            double maxPrice,
            boolean ascending) {
        Long userId = getCurrentUser.getCurrentUserId();
        if (page == 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Sort sort = ascending ? Sort.by(sortedColumn).ascending() : Sort.by(sortedColumn).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Products> result = productRepository.findByUser_IdAndNameContainingIgnoreCaseAndPriceBetween(userId, name,
                minPrice, maxPrice,
                pageable);

        return mapToProductSearchResponceDto(result, page, size);

    }

    public ProductSerchResponceMapping getPublicProductsPaginationWithAscending(int page, int size, String sortedColumn,
            String name,
            double minPrice,
            double maxPrice,
            boolean ascending) {
        if (page == 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Sort sort = ascending ? Sort.by(sortedColumn).ascending() : Sort.by(sortedColumn).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Products> result = productRepository.findByNameContainingIgnoreCaseAndPriceBetween(name, minPrice,
                maxPrice, pageable);

        return mapToProductSearchResponceDto(result, page, size);

    }

    private ProductSerchResponceMapping mapToProductSearchResponceDto(Page<Products> result, int page, int size) {

        List<ProductSearchResponceDto> productSearchResponceDtos = result.getContent()
                .stream()
                .map(product -> {
                    ProductSearchResponceDto dto = new ProductSearchResponceDto();
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setRating(product.getRating());
                    dto.setQuantity(product.getQuantity());
                    dto.setDiscount(product.getDiscount());
                    dto.setFinalPrice(product.getFinalPrice());
                    return dto;
                })
                .collect(Collectors.toList());

        ProductSerchResponceMapping mapping = new ProductSerchResponceMapping();
        mapping.setPageNumber(result.getNumber() + 1); // مهم جدًا
        mapping.setPageSize(result.getSize());
        mapping.setTotalElements(result.getTotalElements());
        mapping.setTotalPages(result.getTotalPages());
        mapping.setData(productSearchResponceDtos);

        return mapping;
    }

    public String DeleteProduct(Long productId) {
        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != UserType.SELLER) {
            throw new RuntimeException("Only Selles users can delete products.");
        }
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        productRepository.delete(product);
        return "Product deleted successfully : " + product.getName();
    }

    public Products getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    public String updateProduct(ProductUpdateDto productUpdateDto) {
        Long userId = getCurrentUser.getCurrentUserId();
        Products existingProduct = productRepository.findById(productUpdateDto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productUpdateDto.getId()));
        if (!existingProduct.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this product.");
        }
        existingProduct.setName(productUpdateDto.getName());
        existingProduct.setDescription(productUpdateDto.getDescription());
        existingProduct.setPrice(productUpdateDto.getPrice());
        existingProduct.setRating(productUpdateDto.getRating());
        existingProduct.setQuantity(productUpdateDto.getQuantity());
        existingProduct.setDiscount(productUpdateDto.getDiscount());
        double finalPrice = calculateFinalPrice(productUpdateDto.getPrice(), productUpdateDto.getDiscount());
        existingProduct.setFinalPrice(finalPrice);
        productRepository.save(existingProduct);
        return "Product updated successfully : " + existingProduct.getName();
    }
}

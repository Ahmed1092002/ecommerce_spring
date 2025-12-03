package com.example.test_ecommerce.ecommerce.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductCreateDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSerchResponceMapping;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductUpdateDto;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.NotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final GetCurrentUser getCurrentUser;

    public ProductService(ProductRepository productRepository, GetCurrentUser getCurrentUser) {
        this.productRepository = productRepository;
        this.getCurrentUser = getCurrentUser;
    }

    public Map<String, Object> createProduct(ProductCreateDto productCreateDto) {
        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != UserType.SELLER) {
            throw new ValidationException("Only Selles users can create products.");
        }
        // You can access the current user details here
        Products product = new Products();
        product.setName(productCreateDto.getName());
        product.setDescription(productCreateDto.getDescription());
        product.setPrice(productCreateDto.getPrice());
        product.setQuantity(productCreateDto.getQuantity());
        product.setSellerProfile(getCurrentUser.getCurrentUser().getSellerProfile());
        product.setDiscount(productCreateDto.getDiscount());

        BigDecimal discountAmount = productCreateDto.getDiscount().divide(new BigDecimal(100)).multiply(productCreateDto.getPrice());
        product.setFinalPrice(productCreateDto.getPrice().subtract(discountAmount));
        productRepository.save(product);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");

        return response;

    }

    public BigDecimal calculateFinalPrice(BigDecimal price, BigDecimal discount) {
        BigDecimal discountAmount = discount.divide(new BigDecimal(100)).multiply(price);
        return price.subtract(discountAmount);
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
        Page<Products> result = productRepository.findBySellerProfile_IdAndNameContainingIgnoreCaseAndPriceBetween(userId, name,
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
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
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
            throw new ValidationException("Only Selles users can delete products.");
        }
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        productRepository.delete(product);
        return "Product deleted successfully : " + product.getName();
    }

    public ProductSearchResponceDto getProductById(Long productId) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        ProductSearchResponceDto dto = new ProductSearchResponceDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDiscount(product.getDiscount());
        dto.setFinalPrice(product.getFinalPrice());
        return dto;
    }

    public HashMap<String, Object> updateProduct(ProductUpdateDto productUpdateDto) {
        Long userId = getCurrentUser.getCurrentUserId();
        Products existingProduct = productRepository.findById(productUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productUpdateDto.getId()));
        if (!existingProduct.getSellerProfile().getUser().getId().equals(userId)) {
            throw new ValidationException("You are not authorized to update this product.");
        }
        existingProduct.setName(productUpdateDto.getName());
        existingProduct.setDescription(productUpdateDto.getDescription());
        existingProduct.setPrice(productUpdateDto.getPrice());
        existingProduct.setQuantity(productUpdateDto.getQuantity());
        existingProduct.setDiscount(productUpdateDto.getDiscount());
        BigDecimal finalPrice = calculateFinalPrice(productUpdateDto.getPrice(), productUpdateDto.getDiscount());
        existingProduct.setFinalPrice(finalPrice);
        productRepository.save(existingProduct);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product updated successfully");

        return response;
    }

    public Products getProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }
}

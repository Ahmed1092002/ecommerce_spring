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
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductUpdateDto;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.repository.projection.ProductWithWishlistStatus;
import com.example.test_ecommerce.ecommerce.repository.projection.BestSellerProductProjection;
import com.example.test_ecommerce.ecommerce.repository.projection.BestSellerProductDto;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.NotFoundException;
import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import jakarta.persistence.Tuple;

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
        Products product = productCreateDto.toProduct();
        product.setSellerProfile(getCurrentUser.getCurrentUser().getSellerProfile());

        BigDecimal finalPrice = calculateFinalPrice(productCreateDto.getPrice(), productCreateDto.getDiscount());
        product.setFinalPrice(finalPrice);
        productRepository.save(product);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product created successfully");

        return response;

    }

    public BigDecimal calculateFinalPrice(BigDecimal price, BigDecimal discount) {
        BigDecimal discountAmount = discount.divide(new BigDecimal(100)).multiply(price);
        return price.subtract(discountAmount);
    }

    public GenericPageResponse<ProductSearchResponceDto> getProductsPaginationWithAscending(int page, int size,
            String sortedColumn,
            String name,
            double minPrice,
            double maxPrice,
            boolean ascending) {
        Long userId = getCurrentUser.getCurrentUserId();
        if (page == 0 || page < 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Sort sort = ascending ? Sort.by(sortedColumn).ascending() : Sort.by(sortedColumn).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Products> result = productRepository.findBySellerProfile_IdAndNameContainingIgnoreCaseAndPriceBetween(
                userId, name,
                minPrice, maxPrice,
                pageable);

        return mapToProductSearchResponceDto(result, page, size);

    }

    public GenericPageResponse<ProductSearchResponceDto> getPublicProductsPaginationWithAscending(int page, int size,
            String sortedColumn,
            String name,
            double minPrice,
            double maxPrice,
            boolean ascending) {
        if (page == 0 || page < 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Sort sort = ascending ? Sort.by(sortedColumn).ascending() : Sort.by(sortedColumn).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != null && currentUserRole == UserType.CUSTOMER) {
            Long customerProfileId = getCurrentUser.getCurrentUserId();
            Page<ProductWithWishlistStatus> result =
                productRepository.findByNameAndPriceWithWishlistStatus(customerProfileId, name, minPrice, maxPrice, pageable);
            return mapToProductSearchResponceDtoWithWishlistProjection(result);
        }

        Page<Products> result = productRepository.findByNameContainingIgnoreCaseAndPriceBetween(name, minPrice, maxPrice, pageable);
        return mapToProductSearchResponceDto(result, page, size);

    }

    private GenericPageResponse<ProductSearchResponceDto> mapToProductSearchResponceDtoWithWishlistProjection(
            Page<ProductWithWishlistStatus> result) {
        List<ProductSearchResponceDto> productSearchResponceDtos = result.getContent()
                .stream()
                .map(projection -> {
                    Products product = projection.getProduct();
                    boolean inWishlist = Boolean.TRUE.equals(projection.getInWishlist());
                    ProductSearchResponceDto dto = new ProductSearchResponceDto();
                    dto.fromEntity(product);
                    dto.setInWishlist(inWishlist);
                    return dto;
                })
                .collect(Collectors.toList());

        GenericPageResponse<ProductSearchResponceDto> mapping = new GenericPageResponse<>();
        mapping.setPageNumber(result.getNumber() + 1);
        mapping.setPageSize(result.getSize());
        mapping.setTotalElements(result.getTotalElements());
        mapping.setTotalPages(result.getTotalPages());
        mapping.setData(productSearchResponceDtos);

        return mapping;
    }

    private GenericPageResponse<ProductSearchResponceDto> mapToProductSearchResponceDto(Page<Products> result, int page,
            int size) {

        List<ProductSearchResponceDto> productSearchResponceDtos = result.getContent()
                .stream()
                .map(product -> {
                    ProductSearchResponceDto dto = new ProductSearchResponceDto();
                    dto.fromEntity(product);

                    return dto;
                })
                .collect(Collectors.toList());

        GenericPageResponse<ProductSearchResponceDto> mapping = new GenericPageResponse<>();
        mapping.setPageNumber(result.getNumber() + 1); // مهم جدًا
        mapping.setPageSize(result.getSize());
        mapping.setTotalElements(result.getTotalElements());
        mapping.setTotalPages(result.getTotalPages());
        mapping.setData(productSearchResponceDtos);

        return mapping;
    }

    @SuppressWarnings("null")
    public HashMap<String, Object> DeleteProduct(Long productId) {
        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != UserType.SELLER) {
            throw new ValidationException("Only Selles users can delete products.");
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        productRepository.delete(product);
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Product deleted successfully : " + product.getName());
        return response;
    }

    public ProductSearchResponceDto getProductById(Long productId) {
        if (getCurrentUser.getCurrentUserRole() == UserType.CUSTOMER) {
            Long customerProfileId = getCurrentUser.getCurrentUserId();
            ProductWithWishlistStatus result =
                productRepository.findByIdWithWishlistStatus(productId, customerProfileId)
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
            Products product = result.getProduct();
            Boolean inWishlist = result.getInWishlist();
            ProductSearchResponceDto dto = new ProductSearchResponceDto();
            dto.fromEntity(product);
            dto.setFinalPrice(product.getFinalPrice());
            dto.setInWishlist(Boolean.TRUE.equals(inWishlist));
            return dto;
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        ProductSearchResponceDto dto = new ProductSearchResponceDto();
        dto.fromEntity(product);
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
        existingProduct.setImage(productUpdateDto.getImage());
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

    public List<ProductSearchResponceDto> getBestSellingProducts() {
        UserType currentUserRole = getCurrentUser.getCurrentUserRole();
        if (currentUserRole != null && currentUserRole == UserType.CUSTOMER) {
            Long customerProfileId = getCurrentUser.getCurrentUserId();
            List<BestSellerProductDto> result =
                    productRepository.findBestSellers(customerProfileId);
            return result.stream().map(dtoProjection -> {
                ProductSearchResponceDto dto = new ProductSearchResponceDto();
                dto.setId(dtoProjection.getId());
                dto.setName(dtoProjection.getName());
                dto.setDescription(dtoProjection.getDescription());
                dto.setPrice(new BigDecimal(dtoProjection.getPrice()));
                dto.setQuantity(dtoProjection.getQuantity());
                dto.setDiscount(new BigDecimal(dtoProjection.getDiscount()));
                dto.setImage(dtoProjection.getImage());
                dto.setFinalPrice(new BigDecimal(dtoProjection.getFinalPrice()));
                dto.setInWishlist(Boolean.TRUE.equals(dtoProjection.getInWishlist()));
                return dto;
            }).collect(Collectors.toList());
        }
        List<Products> bestSellers = productRepository.findForPublicBestSellers();
        return bestSellers.stream().map(product -> {
            ProductSearchResponceDto responseDto = new ProductSearchResponceDto();
            responseDto.fromEntity(product);
            return responseDto;
        }).collect(Collectors.toList());
    }
}

package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Products;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
        Page<Products> findBySellerProfile_IdAndNameContainingIgnoreCase(Long sellerProfileId, String q,
                        Pageable pageable);

        Page<Products> findBySellerProfile_IdAndNameContainingIgnoreCaseAndPriceBetween(Long sellerProfileId,
                        String name, Double minPrice, Double maxPrice, Pageable pageable);

        // create serch function for customer not based on user id contin serch by name
        // handle
        Page<Products> findByNameContainingIgnoreCaseAndPriceBetween(String name, Double minPrice, Double maxPrice,
                        Pageable pageable);

        // Count products by seller profile ID
        Long countBySellerProfileId(Long sellerProfileId);

        @Query(value = "SELECT p.* " +
                        "FROM products p " +
                        "LEFT JOIN order_items oi ON p.id = oi.product_id " +
                        "GROUP BY p.id " +
                        "HAVING COUNT(oi.product_id) > 0 " +
                        "ORDER BY COUNT(oi.product_id) DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<Products> findBestSellers();

}

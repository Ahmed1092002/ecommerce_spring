package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Products;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import com.example.test_ecommerce.ecommerce.repository.projection.ProductWithWishlistStatus;
import com.example.test_ecommerce.ecommerce.repository.projection.BestSellerProductProjection;
import com.example.test_ecommerce.ecommerce.repository.projection.BestSellerProductDto;

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

       @Query("SELECT p as product, CASE WHEN w.id IS NOT NULL THEN true ELSE false END as inWishlist " +
              "FROM Products p " +
              "LEFT JOIN WishlistItem w ON w.product = p AND w.customerProfile.id = :customerProfileId " +
              "WHERE lower(p.name) LIKE lower(concat('%', :name, '%')) " +
              "AND p.price BETWEEN :minPrice AND :maxPrice")
       Page<ProductWithWishlistStatus> findByNameAndPriceWithWishlistStatus(
              @Param("customerProfileId") Long customerProfileId,
              @Param("name") String name,
              @Param("minPrice") Double minPrice,
              @Param("maxPrice") Double maxPrice,
              Pageable pageable);

        @Query("""
                SELECT p AS product,
                       CASE WHEN w.id IS NOT NULL THEN true ELSE false END AS inWishlist
                FROM Products p
                LEFT JOIN WishlistItem w
                       ON w.product = p
                      AND w.customerProfile.id = :customerProfileId
                WHERE p.id = :productId
            """)
        Optional<ProductWithWishlistStatus> findByIdWithWishlistStatus(
                @Param("productId") Long productId,
                @Param("customerProfileId") Long customerProfileId);

        // Count products by seller profile ID
        Long countBySellerProfileId(Long sellerProfileId);

       @Query(value = """
              SELECT p.id as id, p.name as name, p.description as description, p.price as price, p.quantity as quantity, p.discount as discount, p.image as image, p.final_price as finalPrice, p.seller_profile_id as sellerProfileId,
                     CASE WHEN w.id IS NOT NULL THEN true ELSE false END as inWishlist
              FROM products p
              LEFT JOIN order_items oi ON p.id = oi.product_id
              LEFT JOIN wishlist_item w ON w.product_id = p.id AND w.customer_profile_id = :customerProfileId
              GROUP BY p.id, w.id
              HAVING COUNT(oi.product_id) > 0
              ORDER BY COUNT(oi.product_id) DESC
              LIMIT 10
              """, nativeQuery = true)
       List<BestSellerProductDto> findBestSellers(@Param("customerProfileId") Long customerProfileId);

        @Query(value = "SELECT p.*" +
                        "FROM products p " +
                        "LEFT JOIN order_items oi ON p.id = oi.product_id " +
                        "GROUP BY p.id " +
                        "HAVING COUNT(oi.product_id) > 0 " +
                        "ORDER BY COUNT(oi.product_id) DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<Products> findForPublicBestSellers();

}

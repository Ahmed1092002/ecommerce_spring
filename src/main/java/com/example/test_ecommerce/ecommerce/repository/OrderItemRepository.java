package com.example.test_ecommerce.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.OrderItem;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all items for an order
    List<OrderItem> findByOrder_Id(Long orderId);

    // Find items by seller profile
    Page<OrderItem> findBySellerProfile_Id(Long sellerProfileId, Pageable pageable);

    // Find items by seller and status
    Page<OrderItem> findBySellerProfile_IdAndItemStatus(Long sellerProfileId, OrderItemStatus status,
            Pageable pageable);

    // Find seller's items in a specific order
    List<OrderItem> findByOrder_IdAndSellerProfile_Id(Long orderId, Long sellerProfileId);

    // Count items by seller
    Long countBySellerProfile_Id(Long sellerProfileId);

    // Check if item belongs to seller
    boolean existsByIdAndSellerProfile_Id(Long itemId, Long sellerProfileId);

    // Get item with seller validation
    Optional<OrderItem> findByIdAndSellerProfile_Id(Long itemId, Long sellerProfileId);

    // Custom query to get items with order details for seller
    @Query("SELECT oi FROM OrderItem oi " +
             "JOIN FETCH oi.order o " +
             "JOIN FETCH oi.product p " +
             "JOIN FETCH oi.sellerProfile sp " +
             "WHERE oi.sellerProfile.id = :sellerId " +
             "ORDER BY o.createdAt DESC, oi.id DESC")
    Page<OrderItem> findBySellerWithOrderDetails(@Param("sellerId") Long sellerId, Pageable pageable);
}

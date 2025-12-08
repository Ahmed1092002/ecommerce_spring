package com.example.test_ecommerce.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Order;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;
import com.example.test_ecommerce.ecommerce.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        // Find orders by customer profile ID with items eagerly fetched
        @Query("SELECT DISTINCT o FROM Order o " +
                        "LEFT JOIN FETCH o.items " +
                        "WHERE o.customerProfile.id = :customerProfileId " +
                        "ORDER BY o.createdAt DESC")
        List<Order> findByCustomerProfileIdWithItems(@Param("customerProfileId") Long customerProfileId);

        // Find customer orders with search by order number or product name
        @Query("SELECT DISTINCT o FROM Order o " +
                        "LEFT JOIN o.items oi " +
                        "LEFT JOIN oi.product p " +
                        "WHERE o.customerProfile.id = :customerProfileId " +
                        "AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                        "ORDER BY o.createdAt DESC")
        Page<Order> findByCustomerProfile_IdWithSearch(@Param("customerProfileId") Long customerProfileId,
                        @Param("search") String search, Pageable pageable);

        // Find orders that contain items from a specific seller
        @Query("SELECT DISTINCT o FROM Order o " +
                        "JOIN o.items oi " +
                        "WHERE oi.sellerProfile.id = :sellerProfileId " +
                        "ORDER BY o.createdAt DESC")
        Page<Order> findOrdersBySellerProfileId(@Param("sellerProfileId") Long sellerProfileId, Pageable pageable);

        // Find seller orders with search by order number or product name
        @Query("SELECT DISTINCT o FROM Order o " +
                        "JOIN o.items oi " +
                        "LEFT JOIN oi.product p " +
                        "WHERE oi.sellerProfile.id = :sellerProfileId " +
                        "AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                        "ORDER BY o.createdAt DESC")
        Page<Order> findOrdersBySellerProfileIdWithSearch(@Param("sellerProfileId") Long sellerProfileId,
                        @Param("search") String search, Pageable pageable);

        // Find orders by seller and item status
        @Query("SELECT DISTINCT o FROM Order o " +
                        "JOIN o.items oi " +
                        "WHERE oi.sellerProfile.id = :sellerProfileId " +
                        "AND oi.itemStatus = :status " +
                        "ORDER BY o.createdAt DESC")
        Page<Order> findOrdersBySellerProfileIdAndItemStatus(
                        @Param("sellerProfileId") Long sellerProfileId,
                        @Param("status") OrderItemStatus status,
                        Pageable pageable);

        // Find seller orders with search and status filter
        @Query("SELECT DISTINCT o FROM Order o " +
                        "JOIN o.items oi " +
                        "LEFT JOIN oi.product p " +
                        "WHERE oi.sellerProfile.id = :sellerProfileId " +
                        "AND oi.itemStatus = :status " +
                        "AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                        "ORDER BY o.createdAt DESC")
        Page<Order> findOrdersBySellerProfileIdAndItemStatusWithSearch(
                        @Param("sellerProfileId") Long sellerProfileId,
                        @Param("status") OrderItemStatus status,
                        @Param("search") String search,
                        Pageable pageable);

        // Find a specific order with items for seller
        @Query("SELECT DISTINCT o FROM Order o " +
                        "LEFT JOIN FETCH o.items oi " +
                        "WHERE o.id = :orderId " +
                        "AND EXISTS (SELECT 1 FROM OrderItem oi2 WHERE oi2.order = o AND oi2.sellerProfile.id = :sellerProfileId)")
        Optional<Order> findOrderByIdForSeller(@Param("orderId") Long orderId,
                        @Param("sellerProfileId") Long sellerProfileId);

        // Find orders by customer profile ID (paginated)
        Page<Order> findByCustomerProfile_Id(Long customerProfileId, Pageable pageable);

        // Find by order number
        Optional<Order> findByOrderNumber(String orderNumber);

        // Find by customer and status
        Page<Order> findByCustomerProfile_IdAndOrderStatus(Long customerProfileId, OrderStatus status,
                        Pageable pageable);

        // Count orders by customer
        Long countByCustomerProfile_Id(Long customerProfileId);

        // Check if order exists for customer
        boolean existsByIdAndCustomerProfile_Id(Long orderId, Long customerProfileId);
}

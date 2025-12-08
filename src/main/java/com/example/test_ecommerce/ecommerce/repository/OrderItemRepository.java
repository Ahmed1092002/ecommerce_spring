package com.example.test_ecommerce.ecommerce.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    List<OrderItem> findBySellerWithOrderDetails(@Param("sellerId") Long sellerId, Pageable pageable);

    // Analytics queries

    // Count orders by seller and status
    Long countBySellerProfile_IdAndItemStatus(Long sellerProfileId, OrderItemStatus status);

    // Calculate total revenue for seller
    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses")
    BigDecimal calculateTotalRevenueBySeller(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses);

    // Calculate total revenue for seller within date range
    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses " +
            "AND oi.createdAt >= :startDate " +
            "AND oi.createdAt < :endDate")
    BigDecimal calculateRevenueBySellerAndDateRange(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Count orders for seller within date range
    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.createdAt >= :startDate " +
            "AND oi.createdAt < :endDate")
    Long countOrdersBySellerAndDateRange(@Param("sellerId") Long sellerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get daily sales data
    @Query("SELECT CAST(oi.createdAt AS date) as date, " +
            "SUM(oi.totalPrice) as revenue, " +
            "COUNT(DISTINCT oi.order.id) as orderCount " +
            "FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses " +
            "AND oi.createdAt >= :startDate " +
            "AND oi.createdAt < :endDate " +
            "GROUP BY CAST(oi.createdAt AS date) " +
            "ORDER BY CAST(oi.createdAt AS date) DESC")
    List<Object[]> getDailySalesBySeller(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get monthly sales data
    @Query("SELECT EXTRACT(YEAR FROM oi.createdAt) as year, " +
            "EXTRACT(MONTH FROM oi.createdAt) as month, " +
            "SUM(oi.totalPrice) as revenue, " +
            "COUNT(DISTINCT oi.order.id) as orderCount " +
            "FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses " +
            "AND oi.createdAt >= :startDate " +
            "GROUP BY EXTRACT(YEAR FROM oi.createdAt), EXTRACT(MONTH FROM oi.createdAt) " +
            "ORDER BY EXTRACT(YEAR FROM oi.createdAt) DESC, EXTRACT(MONTH FROM oi.createdAt) DESC")
    List<Object[]> getMonthlySalesBySeller(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses,
            @Param("startDate") LocalDateTime startDate);

    // Get top selling products
    @Query("SELECT oi.product.id, " +
            "oi.product.name, " +
            "SUM(oi.quantity) as totalQuantity, " +
            "SUM(oi.totalPrice) as totalRevenue " +
            "FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses " +
            "AND oi.createdAt >= :startDate " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable); // Calculate total quantity sold by seller

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId " +
            "AND oi.itemStatus IN :statuses")
    Long calculateTotalQuantitySoldBySeller(@Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderItemStatus> statuses);

    // Count distinct orders by seller
    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi " +
            "WHERE oi.sellerProfile.id = :sellerId")
    Long countDistinctOrdersBySeller(@Param("sellerId") Long sellerId);
}

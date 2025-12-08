package com.example.test_ecommerce.ecommerce.dto.UserDto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerAnalyticsResponse {

    // Revenue metrics
    private BigDecimal totalRevenue;
    private BigDecimal totalRevenueThisMonth;
    private BigDecimal totalRevenueLastMonth;
    private BigDecimal revenueGrowthPercentage;

    // Order metrics
    private Long totalOrders;
    private Long totalOrdersThisMonth;
    private Long totalOrdersLastMonth;
    private Long pendingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;

    // Product metrics
    private Long totalProducts;
    private Long totalProductsSold;
    private BigDecimal averageOrderValue;

    // Time-based analysis
    private List<DailySalesDto> dailySales;
    private List<MonthlySalesDto> monthlySales;

    // Top products
    private List<TopProductDto> topSellingProducts;
}

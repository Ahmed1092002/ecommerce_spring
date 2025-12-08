package com.example.test_ecommerce.ecommerce.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.UserDto.DailySalesDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.MonthlySalesDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.SellerAnalyticsResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.TopProductDto;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.OrderItemRepository;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class SellerAnalyticsService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final GetCurrentUser getCurrentUser;

    private static final List<OrderItemStatus> COMPLETED_STATUSES = Arrays.asList(
            OrderItemStatus.SHIPPED,
            OrderItemStatus.DELIVERED);

    public SellerAnalyticsService(OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            GetCurrentUser getCurrentUser) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.getCurrentUser = getCurrentUser;
    }

    /**
     * Get comprehensive analytics for the seller
     */
    public SellerAnalyticsResponse getSellerAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Users user = getCurrentUser.getCurrentUser();
        if (user.getUserType() != UserType.SELLER) {
            throw new ValidationException("Only sellers can access analytics.");
        }

        Long sellerId = user.getId();

        // If no date range specified, default to last 30 days
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SellerAnalyticsResponse analytics = SellerAnalyticsResponse.builder().build();

        // Calculate revenue metrics
        calculateRevenueMetrics(analytics, sellerId);

        // Calculate order metrics
        calculateOrderMetrics(analytics, sellerId);

        // Calculate product metrics
        calculateProductMetrics(analytics, sellerId);

        // Get time-based analysis
        analytics.setDailySales(getDailySales(sellerId, startDate, endDate));
        analytics.setMonthlySales(getMonthlySales(sellerId));

        // Get top selling products
        analytics.setTopSellingProducts(getTopSellingProducts(sellerId, startDate));

        return analytics;
    }

    private void calculateRevenueMetrics(SellerAnalyticsResponse analytics, Long sellerId) {
        // Total revenue (all time)
        BigDecimal totalRevenue = orderItemRepository.calculateTotalRevenueBySeller(
                sellerId, COMPLETED_STATUSES);
        analytics.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // This month's revenue
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).withHour(0).withMinute(0)
                .withSecond(0);
        BigDecimal thisMonthRevenue = orderItemRepository.calculateRevenueBySellerAndDateRange(
                sellerId, COMPLETED_STATUSES, startOfMonth, endOfMonth);
        analytics.setTotalRevenueThisMonth(thisMonthRevenue != null ? thisMonthRevenue : BigDecimal.ZERO);

        // Last month's revenue
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth;
        BigDecimal lastMonthRevenue = orderItemRepository.calculateRevenueBySellerAndDateRange(
                sellerId, COMPLETED_STATUSES, startOfLastMonth, endOfLastMonth);
        analytics.setTotalRevenueLastMonth(lastMonthRevenue != null ? lastMonthRevenue : BigDecimal.ZERO);

        // Calculate growth percentage
        if (thisMonthRevenue != null && lastMonthRevenue != null && lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal growth = thisMonthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            analytics.setRevenueGrowthPercentage(growth.setScale(2, RoundingMode.HALF_UP));
        } else {
            analytics.setRevenueGrowthPercentage(BigDecimal.ZERO);
        }
    }

    private void calculateOrderMetrics(SellerAnalyticsResponse analytics, Long sellerId) {
        // Total distinct orders
        Long totalOrders = orderItemRepository.countDistinctOrdersBySeller(sellerId);
        analytics.setTotalOrders(totalOrders != null ? totalOrders : 0L);

        // This month's orders
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).withHour(0).withMinute(0)
                .withSecond(0);
        Long thisMonthOrders = orderItemRepository.countOrdersBySellerAndDateRange(
                sellerId, startOfMonth, endOfMonth);
        analytics.setTotalOrdersThisMonth(thisMonthOrders != null ? thisMonthOrders : 0L);

        // Last month's orders
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth;
        Long lastMonthOrders = orderItemRepository.countOrdersBySellerAndDateRange(
                sellerId, startOfLastMonth, endOfLastMonth);
        analytics.setTotalOrdersLastMonth(lastMonthOrders != null ? lastMonthOrders : 0L);

        // Orders by status
        Long pendingOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.PENDING);
        analytics.setPendingOrders(pendingOrders != null ? pendingOrders : 0L);

        Long shippedOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.SHIPPED);
        analytics.setShippedOrders(shippedOrders != null ? shippedOrders : 0L);

        Long deliveredOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.DELIVERED);
        analytics.setDeliveredOrders(deliveredOrders != null ? deliveredOrders : 0L);

        Long cancelledOrders = orderItemRepository.countBySellerProfile_IdAndItemStatus(
                sellerId, OrderItemStatus.CANCELED);
        analytics.setCancelledOrders(cancelledOrders != null ? cancelledOrders : 0L);
    }

    private void calculateProductMetrics(SellerAnalyticsResponse analytics, Long sellerId) {
        // Total products
        Long totalProducts = productRepository.countBySellerProfileId(sellerId);
        analytics.setTotalProducts(totalProducts != null ? totalProducts : 0L);

        // Total products sold (quantity)
        Long totalProductsSold = orderItemRepository.calculateTotalQuantitySoldBySeller(
                sellerId, COMPLETED_STATUSES);
        analytics.setTotalProductsSold(totalProductsSold != null ? totalProductsSold : 0L);

        // Average order value
        BigDecimal totalRevenue = analytics.getTotalRevenue();
        Long totalOrders = analytics.getTotalOrders();
        if (totalOrders != null && totalOrders > 0) {
            BigDecimal avgOrderValue = totalRevenue.divide(
                    BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
            analytics.setAverageOrderValue(avgOrderValue);
        } else {
            analytics.setAverageOrderValue(BigDecimal.ZERO);
        }
    }

    private List<DailySalesDto> getDailySales(Long sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> dailySalesData = orderItemRepository.getDailySalesBySeller(
                sellerId, COMPLETED_STATUSES, startDate, endDate);

        List<DailySalesDto> dailySales = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Object[] row : dailySalesData) {
            // Convert java.sql.Date to LocalDate
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            BigDecimal revenue = (BigDecimal) row[1];
            Long orderCount = (Long) row[2];

            dailySales.add(DailySalesDto.builder()
                    .date(date.format(formatter))
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .orderCount(orderCount != null ? orderCount : 0L)
                    .build());
        }

        return dailySales;
    }

    private List<MonthlySalesDto> getMonthlySales(Long sellerId) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        List<Object[]> monthlySalesData = orderItemRepository.getMonthlySalesBySeller(
                sellerId, COMPLETED_STATUSES, startDate);

        List<MonthlySalesDto> monthlySales = new ArrayList<>();

        for (Object[] row : monthlySalesData) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            BigDecimal revenue = (BigDecimal) row[2];
            Long orderCount = (Long) row[3];

            String monthString = YearMonth.of(year, month).format(DateTimeFormatter.ofPattern("yyyy-MM"));

            monthlySales.add(MonthlySalesDto.builder()
                    .month(monthString)
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .orderCount(orderCount != null ? orderCount : 0L)
                    .build());
        }

        return monthlySales;
    }

    private List<TopProductDto> getTopSellingProducts(Long sellerId, LocalDateTime startDate) {
        Pageable pageable = PageRequest.of(0, 10); // Top 10 products
        List<Object[]> topProductsData = orderItemRepository.getTopSellingProducts(
                sellerId, COMPLETED_STATUSES, startDate, pageable);

        List<TopProductDto> topProducts = new ArrayList<>();

        for (Object[] row : topProductsData) {
            Long productId = (Long) row[0];
            String productName = (String) row[1];
            Long quantitySold = (Long) row[2];
            BigDecimal totalRevenue = (BigDecimal) row[3];

            topProducts.add(TopProductDto.builder()
                    .productId(productId)
                    .productName(productName)
                    .quantitySold(quantitySold != null ? quantitySold : 0L)
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .build());
        }

        return topProducts;
    }
}

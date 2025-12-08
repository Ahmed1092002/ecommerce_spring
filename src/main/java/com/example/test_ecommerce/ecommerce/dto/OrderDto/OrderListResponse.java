package com.example.test_ecommerce.ecommerce.dto.OrderDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListResponse {
    private List<OrderResponse> orders;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
}

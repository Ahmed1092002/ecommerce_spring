package com.example.test_ecommerce.ecommerce.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderItemResponse;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderListResponse;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderResponse;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;
import com.example.test_ecommerce.ecommerce.services.OrderService;

@RestController
@RequestMapping("/api/seller/orders")
@PreAuthorize("hasRole('SELLER')")
public class SellerOrderController {

    private final OrderService orderService;

    public SellerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> getSellerOrders(
            @RequestParam(required = false) OrderItemStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        OrderListResponse response = orderService.getSellerOrders(status, search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getSellerOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getSellerOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getSellerOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponse> items = orderService.getSellerOrderItems(orderId);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/items/{itemId}/ship")
    public ResponseEntity<Map<String, String>> shipOrderItem(@PathVariable Long itemId) {
        Map<String, String> response = orderService.shipOrderItem(itemId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}/deliver")
    public ResponseEntity<Map<String, String>> deliverOrderItem(@PathVariable Long itemId) {
        Map<String, String> response = orderService.deliverOrderItem(itemId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Map<String, String>> shipAllItemsInOrder(@PathVariable Long orderId) {
        Map<String, String> response = orderService.shipAllSellerItemsInOrder(orderId);
        return ResponseEntity.ok(response);
    }
}

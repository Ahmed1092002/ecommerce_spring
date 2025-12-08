package com.example.test_ecommerce.ecommerce.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ProductOutOfStockException;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.CheckoutRequest;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderItemResponse;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderListResponse;
import com.example.test_ecommerce.ecommerce.dto.OrderDto.OrderResponse;
import com.example.test_ecommerce.ecommerce.entitiy.Address;
import com.example.test_ecommerce.ecommerce.entitiy.Cart;
import com.example.test_ecommerce.ecommerce.entitiy.CartItem;
import com.example.test_ecommerce.ecommerce.entitiy.Order;
import com.example.test_ecommerce.ecommerce.entitiy.OrderItem;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.OrderItemStatus;
import com.example.test_ecommerce.ecommerce.enums.OrderStatus;
import com.example.test_ecommerce.ecommerce.repository.AddressRepository;
import com.example.test_ecommerce.ecommerce.repository.CartItemRepository;
import com.example.test_ecommerce.ecommerce.repository.CartRepository;
import com.example.test_ecommerce.ecommerce.repository.OrderItemRepository;
import com.example.test_ecommerce.ecommerce.repository.OrderRepository;
import com.example.test_ecommerce.ecommerce.repository.ProductRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final GetCurrentUser getCurrentUser;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            GetCurrentUser getCurrentUser) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.getCurrentUser = getCurrentUser;
    }

    // ==================== CUSTOMER METHODS ====================

    @Transactional
    public OrderResponse createOrderFromCart(CheckoutRequest request) {
        Users user = getCurrentUser.getCurrentUser();
        Long customerProfileId = user.getCustomerProfile().getId();

        // Get customer's cart
        Cart cart = cartRepository.findByCustomerProfile_Id(customerProfileId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new ValidationException("Cart is empty");
        }

        // Get cart items
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new ValidationException("No items in cart");
        }

        // Validate stock availability for all items
        validateStockAvailability(cartItems);

        // Validate addresses
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ValidationException("Shipping address not found"));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new ValidationException("Unauthorized access to address");
        }

        Address billingAddress = shippingAddress; // Default to shipping
        if (request.getBillingAddressId() != null) {
            billingAddress = addressRepository.findById(request.getBillingAddressId())
                    .orElseThrow(() -> new ValidationException("Billing address not found"));

            if (!billingAddress.getUser().getId().equals(user.getId())) {
                throw new ValidationException("Unauthorized access to billing address");
            }
        }

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerProfile(user.getCustomerProfile());
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus(OrderStatus.PENDING);

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Create order items and reduce stock
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Products product = cartItem.getProduct();

            // Reduce stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setSellerProfile(product.getSellerProfile());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getFinalPrice());

            // Calculate discount
            BigDecimal originalPrice = product.getPrice();
            BigDecimal discountAmount = originalPrice.subtract(product.getFinalPrice());
            orderItem.setDiscountAtPurchase(discountAmount);
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setItemStatus(OrderItemStatus.PAID);

            return orderItem;
        }).collect(Collectors.toList());

        // Calculate order totals
        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity())));
            totalDiscount = totalDiscount
                    .add(item.getDiscountAtPurchase().multiply(new BigDecimal(item.getQuantity())));
        }

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);

        order.setTotalAmount(totalAmount);
        order.setTotalDiscount(totalDiscount);
        order.setFinalAmount(finalAmount);
        order.setItems(orderItems);

        // Save order (cascades to order items)
        orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteAll(cartItems);

        // Convert to response
        OrderResponse response = new OrderResponse();
        response.fromOrder(order);
        return response;
    }

    public OrderListResponse getCustomerOrders(String search, Pageable pageable) {
        Users user = getCurrentUser.getCurrentUser();
        Long customerProfileId = user.getCustomerProfile().getId();

        // Fetch orders with search filter
        Page<Order> orderPage;
        if (search != null && !search.trim().isEmpty()) {
            orderPage = orderRepository.findByCustomerProfile_IdWithSearch(customerProfileId, search, pageable);
        } else {
            orderPage = orderRepository.findByCustomerProfile_Id(customerProfileId, pageable);
        }

        // Get order IDs from current page
        List<Long> orderIds = orderPage.getContent().stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // Fetch complete orders with items in a single query
        List<Order> ordersWithItems = orderIds.isEmpty() ? List.of()
                : orderRepository.findByCustomerProfileIdWithItems(customerProfileId).stream()
                        .filter(o -> orderIds.contains(o.getId()))
                        .collect(Collectors.toList());

        List<OrderResponse> orderResponses = ordersWithItems.stream()
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    response.fromOrder(order);
                    return response;
                })
                .collect(Collectors.toList());

        return new OrderListResponse(
                orderResponses,
                orderPage.getNumber(),
                orderPage.getTotalPages(),
                orderPage.getTotalElements(),
                orderPage.getSize());
    }

    public OrderResponse getOrderById(Long orderId) {
        Users user = getCurrentUser.getCurrentUser();
        Long customerProfileId = user.getCustomerProfile().getId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found"));

        // Validate ownership
        if (!order.getCustomerProfile().getId().equals(customerProfileId)) {
            throw new ValidationException("Unauthorized access to order");
        }

        OrderResponse response = new OrderResponse();
        response.fromOrder(order);
        return response;
    }

    @Transactional
    public Map<String, String> cancelOrder(Long orderId) {
        Users user = getCurrentUser.getCurrentUser();
        Long customerProfileId = user.getCustomerProfile().getId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found"));

        // Validate ownership
        if (!order.getCustomerProfile().getId().equals(customerProfileId)) {
            throw new ValidationException("Unauthorized access to order");
        }

        // Can only cancel pending orders
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new ValidationException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        // Return stock for all items
        for (OrderItem item : order.getItems()) {
            Products product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);

            // Update item status
            item.setItemStatus(OrderItemStatus.CANCELED);
        }

        // Update order status
        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return Map.of("message", "Order canceled successfully");
    }

    // ==================== SELLER METHODS ====================

    public OrderListResponse getSellerOrders(OrderItemStatus status, String search, Pageable pageable) {
        Users user = getCurrentUser.getCurrentUser();
        Long sellerProfileId = user.getSellerProfile().getId();

        Page<Order> orderPage;

        // Apply search and status filters
        if (search != null && !search.trim().isEmpty()) {
            if (status != null) {
                orderPage = orderRepository.findOrdersBySellerProfileIdAndItemStatusWithSearch(
                        sellerProfileId, status, search, pageable);
            } else {
                orderPage = orderRepository.findOrdersBySellerProfileIdWithSearch(
                        sellerProfileId, search, pageable);
            }
        } else {
            if (status != null) {
                orderPage = orderRepository.findOrdersBySellerProfileIdAndItemStatus(
                        sellerProfileId, status, pageable);
            } else {
                orderPage = orderRepository.findOrdersBySellerProfileId(
                        sellerProfileId, pageable);
            }
        }

        // Convert orders to responses, filtering items to show only seller's items
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    response.fromOrder(order);

                    // Filter items to only show seller's items
                    List<OrderItemResponse> sellerItems = response.getItems().stream()
                            .filter(item -> item.getSellerId().equals(sellerProfileId))
                            .collect(Collectors.toList());
                    response.setItems(sellerItems);

                    // Recalculate amounts for seller's items only
                    BigDecimal sellerTotal = sellerItems.stream()
                            .map(OrderItemResponse::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal sellerDiscount = sellerItems.stream()
                            .map(item -> item.getDiscountAtPurchase()
                                    .multiply(new BigDecimal(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    response.setTotalAmount(sellerTotal.add(sellerDiscount));
                    response.setTotalDiscount(sellerDiscount);
                    response.setFinalAmount(sellerTotal);

                    return response;
                })
                .collect(Collectors.toList());

        return new OrderListResponse(
                orderResponses,
                orderPage.getNumber(),
                orderPage.getTotalPages(),
                orderPage.getTotalElements(),
                orderPage.getSize());
    }

    public OrderResponse getSellerOrderById(Long orderId) {
        Users user = getCurrentUser.getCurrentUser();
        Long sellerProfileId = user.getSellerProfile().getId();

        Order order = orderRepository.findOrderByIdForSeller(orderId, sellerProfileId)
                .orElseThrow(() -> new ValidationException("Order not found or no items from this seller"));

        OrderResponse response = new OrderResponse();
        response.fromOrder(order);

        // Filter items to only show seller's items
        List<OrderItemResponse> sellerItems = response.getItems().stream()
                .filter(item -> item.getSellerId().equals(sellerProfileId))
                .collect(Collectors.toList());
        response.setItems(sellerItems);

        // Recalculate amounts for seller's items only
        BigDecimal sellerTotal = sellerItems.stream()
                .map(OrderItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sellerDiscount = sellerItems.stream()
                .map(item -> item.getDiscountAtPurchase()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalAmount(sellerTotal.add(sellerDiscount));
        response.setTotalDiscount(sellerDiscount);
        response.setFinalAmount(sellerTotal);

        return response;
    }

    public List<OrderItemResponse> getSellerOrderItems(Long orderId) {
        Users user = getCurrentUser.getCurrentUser();
        Long sellerProfileId = user.getSellerProfile().getId();

        List<OrderItem> items = orderItemRepository.findByOrder_IdAndSellerProfile_Id(orderId, sellerProfileId);

        return items.stream()
                .map(item -> {
                    OrderItemResponse response = new OrderItemResponse();
                    response.fromOrderItem(item);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, String> shipOrderItem(Long itemId) {
        OrderItem item = validateSellerOwnership(itemId);

        if (item.getItemStatus() != OrderItemStatus.PAID) {
            throw new ValidationException("Item must be paid to ship. Current status: " + item.getItemStatus());
        }

        item.setItemStatus(OrderItemStatus.SHIPPED);
        orderItemRepository.save(item);

        Order order = item.getOrder();
        updateOrderStatus(order);

        // Update shipped timestamp if all items shipped
        if (order.getOrderStatus() == OrderStatus.SHIPPED && order.getShippedAt() == null) {
            order.setShippedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        return Map.of("message", "Order item marked as shipped");
    }

    @Transactional
    public Map<String, String> deliverOrderItem(Long itemId) {
        OrderItem item = validateSellerOwnership(itemId);

        if (item.getItemStatus() != OrderItemStatus.SHIPPED) {
            throw new ValidationException("Item must be shipped before delivery");
        }

        item.setItemStatus(OrderItemStatus.DELIVERED);
        orderItemRepository.save(item);

        Order order = item.getOrder();
        updateOrderStatus(order);

        // Update delivered timestamp if all items delivered
        if (order.getOrderStatus() == OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        return Map.of("message", "Order item marked as delivered");
    }

    // ==================== HELPER METHODS ====================

    private void validateStockAvailability(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Products product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new ProductOutOfStockException(
                        "Insufficient stock for product: " + product.getName() +
                                ". Available: " + product.getQuantity());
            }
        }
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "ORD-" + timestamp + "-" + random;
    }

    private OrderItem validateSellerOwnership(Long itemId) {
        Users user = getCurrentUser.getCurrentUser();
        Long sellerProfileId = user.getSellerProfile().getId();

        OrderItem item = orderItemRepository.findByIdAndSellerProfile_Id(itemId, sellerProfileId)
                .orElseThrow(() -> new ValidationException("Order item not found or unauthorized"));

        return item;
    }

    private void updateOrderStatus(Order order) {
        List<OrderItem> items = order.getItems();

        // Check if all items are canceled
        boolean allCanceled = items.stream().allMatch(item -> item.getItemStatus() == OrderItemStatus.CANCELED);
        if (allCanceled) {
            order.setOrderStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            return;
        }

        // Check if all items are delivered
        boolean allDelivered = items.stream().allMatch(item -> item.getItemStatus() == OrderItemStatus.DELIVERED);
        if (allDelivered) {
            order.setOrderStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            return;
        }

        // Check if all items are shipped
        boolean allShipped = items.stream().allMatch(item -> item.getItemStatus() == OrderItemStatus.SHIPPED ||
                item.getItemStatus() == OrderItemStatus.DELIVERED);
        if (allShipped) {
            order.setOrderStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
            return;
        }
    }

    @Transactional
    public Map<String, String> shipAllSellerItemsInOrder(Long orderId) {
        Users user = getCurrentUser.getCurrentUser();
        Long sellerProfileId = user.getSellerProfile().getId();

        // Get all seller's items in this order
        List<OrderItem> items = orderItemRepository
                .findByOrder_IdAndSellerProfile_Id(orderId, sellerProfileId);

        if (items.isEmpty()) {
            throw new ValidationException("No items found for this seller in this order");
        }

        // Ship all items at once
        for (OrderItem item : items) {
            if (item.getItemStatus() == OrderItemStatus.PAID) {
                item.setItemStatus(OrderItemStatus.SHIPPED);
            }
        }

        orderItemRepository.saveAll(items);

        // Update order status
        Order order = items.get(0).getOrder();
        updateOrderStatus(order);

        return Map.of("message", "All items shipped successfully");
    }

}

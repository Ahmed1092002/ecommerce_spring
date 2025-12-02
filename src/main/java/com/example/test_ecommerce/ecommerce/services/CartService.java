package com.example.test_ecommerce.ecommerce.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ProductOutOfStockException;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemDto;
import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemDtoList;
import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemsResponceDto;
import com.example.test_ecommerce.ecommerce.entitiy.Cart;
import com.example.test_ecommerce.ecommerce.entitiy.CartItem;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.CartStatus;
import com.example.test_ecommerce.ecommerce.repository.CartItemRepository;
import com.example.test_ecommerce.ecommerce.repository.CartRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;
import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    public final GetCurrentUser getCurrentUser;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            GetCurrentUser getCurrentUser, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.getCurrentUser = getCurrentUser;
        this.productService = productService;
    }

    public Cart createCart() {

        Users user = getCurrentUser.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId());

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setStatus(CartStatus.ACTIVE);

            cart = cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public String addItemToCart(CartItemDto cartItem) {
        Cart cart = createCart();
        ProductSearchResponceDto productDto = productService.getProductById(cartItem.getProductId());
        if (productDto.getQuantity() < cartItem.getQuantity()) {
            throw new ProductOutOfStockException("Insufficient stock. Available: " + productDto.getQuantity());
        }
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productDto.getId());

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            if (productDto.getQuantity() < newQuantity) {
                throw new ProductOutOfStockException("Insufficient stock. Available: " + productDto.getQuantity());
            }

            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(existingItem.getPrice().multiply(new BigDecimal(existingItem.getQuantity())));
            cartItemRepository.save(existingItem);
            return "Item quantity updated successfully.";
        } else {
            BigDecimal price = productService.calculateFinalPrice(
                    productDto.getPrice(),
                    productDto.getDiscount());
            Products product = productService.getProductEntityById(cartItem.getProductId());

            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setPrice(price);
            newCartItem.setQuantity(cartItem.getQuantity());
            newCartItem.setTotalPrice(price.multiply(new BigDecimal(cartItem.getQuantity())));

            cartItemRepository.save(newCartItem);
            return "Item added to cart successfully.";
        }

    }

    public CartItemsResponceDto getCart() {
        Users user = getCurrentUser.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId());
        CartItemsResponceDto responseDto = new CartItemsResponceDto();

        if (cart == null) {
            responseDto.setCartItems(List.of());
            responseDto.setTotalItems(0);
            responseDto.setTotalCartPrice(new BigDecimal(0));
            responseDto.setTotalQuantity(0);
            responseDto.setTotalDiscount(new BigDecimal(0));
            return responseDto;
        }
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDtoList> responceDtos = cartItems.stream().map(item -> {
            CartItemDtoList dto = new CartItemDtoList();
            dto.setCartItemId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setTotalPrice(item.getTotalPrice());
            return dto;
        }).toList();

        responseDto.setCartItems(responceDtos);
        responseDto.setTotalItems(responceDtos.size());
        BigDecimal totalPrice = responceDtos.stream()
                .map((item) -> item.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        responseDto.setTotalCartPrice(totalPrice);
        int totalQuantity = responceDtos.stream()
                .mapToInt((item) -> item.getQuantity())
                .sum();
        responseDto.setTotalQuantity(totalQuantity);
        BigDecimal totalDiscount = cartItems.stream()
                .map((item) -> {
                    BigDecimal originalPrice = item.getProduct().getPrice()
                            .multiply(new BigDecimal(item.getQuantity()));
                    return originalPrice.subtract(item.getTotalPrice());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        responseDto.setTotalDiscount(totalDiscount);

        return responseDto;
    }

    @Transactional

    public String deleteItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ValidationException("Cart item not found with id: " + cartItemId));
        Users currentUser = getCurrentUser.getCurrentUser();
        if (!cartItem.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new ValidationException("Unauthorized access to cart item");
        }
        cartItemRepository.delete(cartItem);
        return "Cart item deleted successfully.";
    }

    @Transactional
    public String editItemQuantity(Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            return deleteItemFromCart(cartItemId);
        }
        // SETS exact quantity
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ValidationException("Cart item not found with id: " + cartItemId));

        // Add stock validation
        ProductSearchResponceDto product = productService.getProductById(cartItem.getProduct().getId());
        if (product.getQuantity() < quantity) {
            throw new ProductOutOfStockException("Insufficient stock. Available: " + product.getQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal(quantity)));
        cartItemRepository.save(cartItem);
        return "Cart item quantity updated successfully.";
    }

    @Transactional
    public String clearCart() {
        Users user = getCurrentUser.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId());

        if (cart != null) {
            List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(cartItems);
            return "Cart cleared successfully.";
        }
        return "Cart is already empty.";
    }

}

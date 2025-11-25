package com.example.test_ecommerce.ecommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public String addItemToCart(CartItemDto cartItem) {
        Cart cart = createCart();
        Products product = productService.getProductById(cartItem.getProductId());
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            existingItem.setTotalPrice(existingItem.getPrice() * existingItem.getQuantity());
            cartItemRepository.save(existingItem);
            return "Item quantity updated successfully.";
        } else {
            double price = productService.calculateFinalPrice(
                    product.getPrice(),
                    product.getDiscount());
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setPrice(price);
            newCartItem.setQuantity(cartItem.getQuantity());
            newCartItem.setTotalPrice(price * cartItem.getQuantity());

            cartItemRepository.save(newCartItem);
            return "Item added to cart successfully.";
        }

    }

    public CartItemsResponceDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDtoList> responceDtos = cartItems.stream().map(item -> {
            CartItemDtoList dto = new CartItemDtoList();
            dto.setProductId(item.getProduct().getId());
            dto.setName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setTotalPrice(item.getTotalPrice());
            return dto;
        }).toList();

        CartItemsResponceDto responseDto = new CartItemsResponceDto();
        responseDto.setCartItems(responceDtos);
        responseDto.setTotalItems(responceDtos.size());
        double totalPrice = responceDtos.stream()
                .mapToDouble((item) -> item.getTotalPrice())
                .sum();
        responseDto.setTotalCartPrice(totalPrice);
        int totalQuantity = responceDtos.stream()
                .mapToInt((item) -> item.getQuantity())
                .sum();
        responseDto.setTotalQuantity(totalQuantity);
        double totalDiscount = cartItems.stream()
                .mapToDouble((item) -> {
                    double originalPrice = item.getProduct().getPrice() * item.getQuantity();
                    return originalPrice - item.getTotalPrice();
                })
                .sum();
        responseDto.setTotalDiscount(totalDiscount);

        return responseDto;
    }

    public String deleteItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        cartItemRepository.delete(cartItem);
        return "Cart item deleted successfully.";
    }

    public String editItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        cartItem.setQuantity(quantity);
        double price = cartItem.getPrice();
        cartItem.setTotalPrice(price * quantity);
        cartItemRepository.save(cartItem);
        return "Cart item quantity updated successfully.";

    }

}

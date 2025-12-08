package com.example.test_ecommerce.ecommerce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.test_ecommerce.ecommerce.dto.CartItemDto.CartItemDto;

@Aspect
@Component
public class CartValidationAspect {

    private static final Logger logger = LoggerFactory.getLogger(CartValidationAspect.class);

    @Before("execution(* com.example.test_ecommerce.ecommerce.services.CartService.addItemToCart(..)) && args(cartItemDto)")
    public void validateCartItem(JoinPoint joinPoint, CartItemDto cartItemDto) {
        logger.info("Validating cart item before adding: {}", cartItemDto);

        if (cartItemDto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (cartItemDto.getQuantity() == null || cartItemDto.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        if (cartItemDto.getQuantity() > 100) {
            logger.warn("Large quantity requested: {}", cartItemDto.getQuantity());
            throw new IllegalArgumentException("Quantity cannot exceed 100 items");
        }
    }
}
package com.example.test_ecommerce.ecommerce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.test_ecommerce.ecommerce.dto.ProductsDto.ProductSearchResponceDto;

@Aspect
@Component
public class StockManagementAspect {

    private static final Logger logger = LoggerFactory.getLogger(StockManagementAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.test_ecommerce.ecommerce.services.CartService.addItemToCart(..))", returning = "result")
    public void logStockChange(JoinPoint joinPoint, Object result) {
        // Use Object to avoid signature mismatch compilation errors if return type changes
        if (result != null) {
            logger.info("Stock updated after cart operation: {}", result);
        } else {
            logger.info("Stock updated after cart operation.");
        }
    }

    // Alert for low stock
    @AfterReturning(pointcut = "execution(* com.example.test_ecommerce.ecommerce.services.ProductService.getProductById(..))", returning = "product")
    public void checkLowStock(JoinPoint joinPoint, ProductSearchResponceDto product) {
        if (product != null && product.getQuantity() < 10) {
            logger.warn("LOW STOCK ALERT: Product {} has only {} items left", product.getName(), product.getQuantity());
        }
    }
}
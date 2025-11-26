package com.example.test_ecommerce.ecommerce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Aspect
@Component
public class AuthorizationAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationAspect.class);
    private final GetCurrentUser getCurrentUser;

    public AuthorizationAspect(GetCurrentUser getCurrentUser) {
        this.getCurrentUser = getCurrentUser;
    }

    // Check seller authorization for product operations
    @Before("execution(* com.example.test_ecommerce.ecommerce.services.ProductService.createProduct(..)) || " +
            "execution(* com.example.test_ecommerce.ecommerce.services.ProductService.updateProduct(..)) || " +
            "execution(* com.example.test_ecommerce.ecommerce.services.ProductService.DeleteProduct(..))")
    public void checkSellerAuthorization(JoinPoint joinPoint) {
        UserType userType = getCurrentUser.getCurrentUserRole();
        if (userType != UserType.SELLER) {
            logger.warn("Unauthorized access attempt by user role: {} to method: {}",
                    userType,
                    joinPoint.getSignature().getName());
            throw new RuntimeException("Only sellers can perform this operation");
        }
        logger.info("Seller authorization verified for method: {}",
                joinPoint.getSignature().getName());
    }
}
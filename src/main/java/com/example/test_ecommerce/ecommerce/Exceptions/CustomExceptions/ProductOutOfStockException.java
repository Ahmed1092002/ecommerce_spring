package com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String message) {
        super(message);
    }
    
}
package com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    
}

package com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
    
}

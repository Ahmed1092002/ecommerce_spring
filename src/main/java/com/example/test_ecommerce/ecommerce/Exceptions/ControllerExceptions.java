package com.example.test_ecommerce.ecommerce.Exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ProductOutOfStockException;
import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.UserCustomExceptions;
import com.example.test_ecommerce.ecommerce.dto.ErrorResponce.ErrorResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptions {

    private ResponseEntity<ErrorResponse> buildErrorResponse(String error, String message, HttpStatus status,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .error(error)
                .message(message)
                .status(status.value())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        return buildErrorResponse("Illegal Argument", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UserCustomExceptions.class)
    public ResponseEntity<ErrorResponse> handleUserCustomExceptions(UserCustomExceptions ex,
            HttpServletRequest request) {
        return buildErrorResponse("User Custom Exception", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String field = ex.getBindingResult().getFieldError() != null ? ex.getBindingResult().getFieldError().getField()
                : "Validation Error";
        String msg = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : ex.getMessage();
        return buildErrorResponse(field, msg, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse("Runtime Exception", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex, HttpServletRequest request) {
        return buildErrorResponse("JWT Exception", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorResponse> handleServletException(ServletException ex, HttpServletRequest request) {
        return buildErrorResponse("Servlet Exception", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleProductOutOfStockException(ProductOutOfStockException ex,
            HttpServletRequest request) {
        return buildErrorResponse("Product Out Of Stock Exception", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }
}

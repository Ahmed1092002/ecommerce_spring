package com.example.test_ecommerce.ecommerce.dto.ErrorResponce;

import java.time.Instant;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String code;

}
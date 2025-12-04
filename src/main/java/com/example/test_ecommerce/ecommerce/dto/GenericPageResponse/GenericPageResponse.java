package com.example.test_ecommerce.ecommerce.dto.GenericPageResponse;

import java.util.List;

import lombok.Data;

@Data
public class GenericPageResponse<T> {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> data;


    
}

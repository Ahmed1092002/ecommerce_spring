package com.example.test_ecommerce.ecommerce.dto.ProductsDto;

import java.util.List;

import lombok.Data;

@Data
public class ProductSerchResponceMapping {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<ProductSearchResponceDto> data;
}

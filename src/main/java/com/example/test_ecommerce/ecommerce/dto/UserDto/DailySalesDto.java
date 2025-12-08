package com.example.test_ecommerce.ecommerce.dto.UserDto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySalesDto {
    private String date;
    private BigDecimal revenue;
    private Long orderCount;
}

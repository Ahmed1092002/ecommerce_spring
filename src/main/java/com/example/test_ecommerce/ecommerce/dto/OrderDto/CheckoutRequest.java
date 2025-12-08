package com.example.test_ecommerce.ecommerce.dto.OrderDto;

import com.example.test_ecommerce.ecommerce.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull(message = "Shipping address is required")
    private Long shippingAddressId;

    private Long billingAddressId; // Optional, defaults to shipping address

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}

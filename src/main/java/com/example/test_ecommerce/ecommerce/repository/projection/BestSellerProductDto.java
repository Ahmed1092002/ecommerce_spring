package com.example.test_ecommerce.ecommerce.repository.projection;

public interface BestSellerProductDto {
    Long getId();
    String getName();
    String getDescription();
    Double getPrice();
    Integer getQuantity();
    Double getDiscount();
    String getImage();
    Double getFinalPrice();
    Long getSellerProfileId();
    Boolean getInWishlist();
}
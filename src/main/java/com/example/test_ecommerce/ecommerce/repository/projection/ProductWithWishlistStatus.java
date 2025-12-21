package com.example.test_ecommerce.ecommerce.repository.projection;

import com.example.test_ecommerce.ecommerce.entitiy.Products;

public interface ProductWithWishlistStatus {
    Products getProduct();
    Boolean getInWishlist();
}
package com.example.test_ecommerce.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.test_ecommerce.ecommerce.entitiy.CustomerProfile;
import com.example.test_ecommerce.ecommerce.entitiy.Products;
import com.example.test_ecommerce.ecommerce.entitiy.WishlistItem;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByCustomerProfileAndProduct(CustomerProfile customerProfile, Products product);

    boolean existsByCustomerProfileAndProduct(CustomerProfile customerProfile, Products product);

    Page<WishlistItem> findByCustomerProfileId(Long customerProfileId, Pageable pageable);
}

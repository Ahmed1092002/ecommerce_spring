package com.example.test_ecommerce.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    public List<CartItem> findByCartId(Long cartId);

    public CartItem findByCartIdAndProductId(Long cartId, Long productId);

}

package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Cart;
import com.example.test_ecommerce.ecommerce.enums.CartStatus;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    public Cart findByUserIdAndStatus(Long userId, CartStatus status);

    public Cart findByUserIdAndStatusNot(Long userId, CartStatus status);

    public Cart findByUserId(Long userId);

    public void deleteById(Long id);

}
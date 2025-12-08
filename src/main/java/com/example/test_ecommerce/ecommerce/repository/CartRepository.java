package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Cart;
import com.example.test_ecommerce.ecommerce.enums.CartStatus;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByCustomerProfile_IdAndStatus(Long customerProfileId, CartStatus status);

    Cart findByCustomerProfile_IdAndStatusNot(Long customerProfileId, CartStatus status);

    Cart findByCustomerProfile_Id(Long customerProfileId);

    public void deleteById(Long id);

}
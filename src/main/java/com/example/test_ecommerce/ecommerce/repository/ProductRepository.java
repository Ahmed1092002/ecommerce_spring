package com.example.test_ecommerce.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Products;
@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    
}

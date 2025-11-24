package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.test_ecommerce.ecommerce.entitiy.Products;
@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    Page<Products> findByUser_IdAndNameContainingIgnoreCase(Long userId, String q, Pageable pageable);
    
}

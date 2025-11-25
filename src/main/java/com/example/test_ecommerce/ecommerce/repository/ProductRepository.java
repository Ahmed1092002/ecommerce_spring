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

        // create serch min price and max price with serch with name
        Page<Products> findByUser_IdAndNameContainingIgnoreCaseAndPriceBetween(Long userId, String name,
                        Double minPrice, Double maxPrice,
                        Pageable pageable);

        // create serch function for customer not based on user id contin serch by name
        // and serch filter
        Page<Products> findByNameContainingIgnoreCaseAndPriceBetween(String name, Double minPrice, Double maxPrice,
                        Pageable pageable);



}

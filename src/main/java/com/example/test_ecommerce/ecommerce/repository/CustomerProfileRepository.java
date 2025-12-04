package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.test_ecommerce.ecommerce.entitiy.CustomerProfile;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

}

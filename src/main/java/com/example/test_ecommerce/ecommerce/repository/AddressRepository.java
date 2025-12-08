package com.example.test_ecommerce.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.test_ecommerce.ecommerce.entitiy.Address;
import com.example.test_ecommerce.ecommerce.entitiy.Users;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findByUserId(Long userId, Pageable pageable);

    Optional<Address> findByUserIdAndIsDefault(Long userId, boolean isDefault);

}

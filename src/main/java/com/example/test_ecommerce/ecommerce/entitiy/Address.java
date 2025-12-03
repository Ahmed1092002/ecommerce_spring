package com.example.test_ecommerce.ecommerce.entitiy;

import com.example.test_ecommerce.ecommerce.enums.AddressType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Simplified: Reference Users directly (not CustomerProfile or SellerProfile)
    // Since customer_profile_id = seller_profile_id = user_id, we only need one column
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType type;
    
    private String label;  // Custom label like "Mom's house", "Main warehouse"
    
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String street;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String zipCode;
    
    @Column(nullable = false)
    private String country;
    
    @Column(nullable = false)
    private Boolean isDefault = false;  // Default address for orders/shipping
}
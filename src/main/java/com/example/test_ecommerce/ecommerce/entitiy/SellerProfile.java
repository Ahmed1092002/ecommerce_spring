package com.example.test_ecommerce.ecommerce.entitiy;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class SellerProfile {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String businessName;

    // Addresses are accessed via user.getAddresses() - no need for direct
    // relationship here

    @OneToMany(mappedBy = "sellerProfile", cascade = CascadeType.ALL)
    private List<Products> products = new ArrayList<>();
}

package com.example.test_ecommerce.ecommerce.controller.AddressController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.AddressRequestDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.AddressResponse;
import com.example.test_ecommerce.ecommerce.services.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seller/addresses")
@PreAuthorize("hasRole('SELLER')")
public class SellerAddressController {

    private final AddressService addressService;

    public SellerAddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Get all addresses for the current seller (paginated)
     * GET /api/seller/addresses?page=1
     */
    @GetMapping
    public ResponseEntity<GenericPageResponse<AddressResponse>> getAddresses(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(addressService.getSellerAddresses(page));
    }

    /**
     * Add a new address
     * POST /api/seller/addresses
     */
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequestDto addressRequestDto) {
        return ResponseEntity.ok(addressService.addSellerAddress(addressRequestDto));
    }

    /**
     * Update an existing address
     * PUT /api/seller/addresses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        return ResponseEntity.ok(addressService.updateSellerAddress(id, addressRequestDto));
    }

    /**
     * Set an address as default
     * PATCH /api/seller/addresses/{id}/default
     */
    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefaultSellerAddress(id));
    }

    /**
     * Delete an address
     * DELETE /api/seller/addresses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AddressResponse> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.removeSellerAddress(id));
    }
}

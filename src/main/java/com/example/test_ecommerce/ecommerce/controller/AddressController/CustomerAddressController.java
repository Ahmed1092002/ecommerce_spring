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
@RequestMapping("/api/customer/addresses")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerAddressController {

    private final AddressService addressService;

    public CustomerAddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Get all addresses for the current customer (paginated)
     * GET /api/customer/addresses?page=1
     */
    @GetMapping
    public ResponseEntity<GenericPageResponse<AddressResponse>> getAddresses(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(addressService.getCustomerAddresses(page));
    }

    /**
     * Get a specific address by ID
     * GET /api/customer/addresses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getCustomerAddressById(id));
    }

    /**
     * Get the default address
     * GET /api/customer/addresses/default
     */
    @GetMapping("/default")
    public ResponseEntity<AddressResponse> getDefaultAddress() {
        return ResponseEntity.ok(addressService.getCustomerAddressByDefault());
    }

    /**
     * Add a new address
     * POST /api/customer/addresses
     */
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequestDto addressRequestDto) {
        return ResponseEntity.ok(addressService.addCustomerAddress(addressRequestDto));
    }

    /**
     * Update an existing address
     * PUT /api/customer/addresses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto addressRequestDto) {
        return ResponseEntity.ok(addressService.updateCustomerAddress(id, addressRequestDto));
    }

    /**
     * Set an address as default
     * PATCH /api/customer/addresses/{id}/default
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setCustomerAddressDefault(id));
    }

    /**
     * Delete an address
     * DELETE /api/customer/addresses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AddressResponse> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.removeCustomerAddress(id));
    }
}

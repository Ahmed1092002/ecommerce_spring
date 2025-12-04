
package com.example.test_ecommerce.ecommerce.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.ValidationException;
import com.example.test_ecommerce.ecommerce.dto.GenericPageResponse.GenericPageResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.AddressRequestDto;
import com.example.test_ecommerce.ecommerce.dto.UserDto.AddressResponse;
import com.example.test_ecommerce.ecommerce.entitiy.Address;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.AddressType;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.AddressRepository;
import com.example.test_ecommerce.ecommerce.utils.GetCurrentUser;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final GetCurrentUser getCurrentUser;

    public AddressService(AddressRepository addressRepository, GetCurrentUser getCurrentUser) {
        this.addressRepository = addressRepository;
        this.getCurrentUser = getCurrentUser;
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validates that the current user has the expected user type
     */
    private void validateUserType(Users user, UserType expectedType, String operation) {
        if (user.getUserType() != expectedType) {
            throw new ValidationException("Only " + expectedType.name().toLowerCase() + "s can " + operation + ".");
        }
    }

    /**
     * Validates that the address belongs to the specified user
     */
    private void validateOwnership(Address address, Users user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new ValidationException("Address not found with id: " + address.getId());
        }
    }

    /**
     * Validates that the address type is appropriate for the user type
     */
    private void validateAddressType(AddressType type, UserType userType) {
        List<AddressType> validTypes;
        if (userType == UserType.CUSTOMER) {
            validTypes = Arrays.asList(AddressType.HOME, AddressType.WORK, AddressType.OTHER);
            if (!validTypes.contains(type)) {
                throw new ValidationException("Invalid address type for customer. Must be HOME, WORK, or OTHER.");
            }
        } else if (userType == UserType.SELLER) {
            validTypes = Arrays.asList(AddressType.STORE, AddressType.WAREHOUSE, AddressType.PICKUP_LOCATION,
                    AddressType.OTHER);
            if (!validTypes.contains(type)) {
                throw new ValidationException(
                        "Invalid address type for seller. Must be STORE, WAREHOUSE, PICKUP_LOCATION, or OTHER.");
            }
        }
    }

    /**
     * Creates an AddressResponse from an Address entity
     */
    private AddressResponse createAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.fromEntity(address);
        return response;
    }

    /**
     * Updates address fields from DTO
     */
    private void updateAddressFields(Address address, AddressRequestDto dto) {
        address.setType(dto.getType());
        address.setLabel(dto.getLabel());
        address.setPhone(dto.getPhone());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setIsDefault(dto.getIsDefault());
    }

    /**
     * Unsets any existing default address for the user
     */
    @Transactional
    private void unsetDefaultAddress(Users user) {
        addressRepository.findByUserIdAndIsDefault(user.getId(), true)
                .ifPresent(address -> {
                    address.setIsDefault(false);
                    addressRepository.save(address);
                });
    }

    /**
     * Retrieves an address by ID and validates ownership
     */
    private Address getAddressWithOwnershipCheck(Long addressId, Users user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ValidationException("Address not found with id: " + addressId));
        validateOwnership(address, user);
        return address;
    }

    // ==================== Customer Address Methods ====================

    public AddressResponse addCustomerAddress(AddressRequestDto addressRequestDto) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "add addresses");
        validateAddressType(addressRequestDto.getType(), UserType.CUSTOMER);

        if (Boolean.TRUE.equals(addressRequestDto.getIsDefault())) {
            unsetDefaultAddress(user);
        }

        Address address = addressRequestDto.toAddress();
        address.setUser(user);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    public AddressResponse removeCustomerAddress(Long addressId) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "remove addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);
        AddressResponse response = createAddressResponse(address);
        addressRepository.delete(address);
        return response;
    }

    public AddressResponse updateCustomerAddress(Long addressId, AddressRequestDto addressRequestDto) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "update addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);

        if (Boolean.TRUE.equals(addressRequestDto.getIsDefault())) {
            unsetDefaultAddress(user);
        }

        updateAddressFields(address, addressRequestDto);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    public GenericPageResponse<AddressResponse> getCustomerAddresses(int page) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "get addresses");

        if (page <= 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Page<Address> addresses = addressRepository.findByUserId(user.getId(), PageRequest.of(page - 1, 10));
        GenericPageResponse<AddressResponse> response = new GenericPageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(addresses.getSize());
        response.setTotalElements(addresses.getTotalElements());
        response.setTotalPages(addresses.getTotalPages());

        addresses.forEach(address -> response.getData().add(createAddressResponse(address)));
        return response;
    }

    public AddressResponse getCustomerAddressById(Long addressId) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "get addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);
        return createAddressResponse(address);
    }

    public AddressResponse getCustomerAddressByDefault() {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "get addresses");

        Address address = addressRepository.findByUserIdAndIsDefault(user.getId(), true)
                .orElseThrow(() -> new ValidationException("No default address found for user"));
        return createAddressResponse(address);
    }

    @Transactional
    public AddressResponse setCustomerAddressDefault(Long addressId) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.CUSTOMER, "set default addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);

        unsetDefaultAddress(user);
        address.setIsDefault(true);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    // ==================== Seller Address Methods ====================

    public AddressResponse addSellerAddress(AddressRequestDto addressRequestDto) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.SELLER, "add addresses");
        validateAddressType(addressRequestDto.getType(), UserType.SELLER);

        if (Boolean.TRUE.equals(addressRequestDto.getIsDefault())) {
            unsetDefaultAddress(user);
        }

        Address address = addressRequestDto.toAddress();
        address.setUser(user);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    public AddressResponse updateSellerAddress(Long addressId, AddressRequestDto addressRequestDto) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.SELLER, "update addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);

        if (Boolean.TRUE.equals(addressRequestDto.getIsDefault())) {
            unsetDefaultAddress(user);
        }

        updateAddressFields(address, addressRequestDto);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    public AddressResponse removeSellerAddress(Long addressId) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.SELLER, "remove addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);
        AddressResponse response = createAddressResponse(address);
        addressRepository.delete(address);
        return response;
    }

    @Transactional
    public AddressResponse setDefaultSellerAddress(Long addressId) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.SELLER, "set default addresses");
        Address address = getAddressWithOwnershipCheck(addressId, user);

        unsetDefaultAddress(user);
        address.setIsDefault(true);
        addressRepository.save(address);
        return createAddressResponse(address);
    }

    public GenericPageResponse<AddressResponse> getSellerAddresses(int page) {
        Users user = getCurrentUser.getCurrentUser();
        validateUserType(user, UserType.SELLER, "get addresses");

        if (page <= 0) {
            throw new IllegalArgumentException("Page index must be greater than 0");
        }

        Page<Address> addresses = addressRepository.findByUserId(user.getId(), PageRequest.of(page - 1, 10));
        GenericPageResponse<AddressResponse> response = new GenericPageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(addresses.getSize());
        response.setTotalPages(addresses.getTotalPages());
        response.setTotalElements(addresses.getTotalElements());

        addresses.forEach(address -> response.getData().add(createAddressResponse(address)));
        return response;
    }
}

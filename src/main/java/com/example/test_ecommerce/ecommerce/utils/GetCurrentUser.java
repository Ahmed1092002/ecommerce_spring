package com.example.test_ecommerce.ecommerce.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component

public class GetCurrentUser {
    private final UserRepository userRepository;

    public GetCurrentUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Returns full Users entity
    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public UserType getCurrentUserRole() {
        return getCurrentUser().getUserType();
    }
}

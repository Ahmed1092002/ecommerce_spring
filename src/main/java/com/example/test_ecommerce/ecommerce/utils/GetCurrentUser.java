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
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Users)) {
            return null;
        }

        Users user = (Users) principal;
        return userRepository.findById(user.getId())
                .orElse(null);
    }

    public String getCurrentUsername() {
        Users user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public Long getCurrentUserId() {
        Users user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public UserType getCurrentUserRole() {
        Users user = getCurrentUser();
        return user != null ? user.getUserType() : null;
    }
}

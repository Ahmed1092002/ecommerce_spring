package com.example.test_ecommerce.ecommerce.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.test_ecommerce.ecommerce.entitiy.Users;

@Service
public class CustomUserDetails implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Users user = userRepository.findByUsernameOrEmail(login, login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + login));
        return user;
    }

}

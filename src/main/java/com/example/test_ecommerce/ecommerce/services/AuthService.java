package com.example.test_ecommerce.ecommerce.services;

import java.util.logging.Logger;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.test_ecommerce.ecommerce.Exceptions.CustomExceptions.UserCustomExceptions;
import com.example.test_ecommerce.ecommerce.dto.UserDto.AuthResponse;
import com.example.test_ecommerce.ecommerce.dto.UserDto.LoginRequest;
import com.example.test_ecommerce.ecommerce.dto.UserDto.RegisterRequest;
import com.example.test_ecommerce.ecommerce.entitiy.Users;
import com.example.test_ecommerce.ecommerce.enums.UserType;
import com.example.test_ecommerce.ecommerce.repository.UserRepository;
import com.example.test_ecommerce.ecommerce.security.jwt.JwtUtiles;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtiles jwtUtiles;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtiles jwtUtiles,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtiles = jwtUtiles;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String userType = registerRequest.getUserType();

        if (userRepository.existsByUsername(username)) {
            throw new UserCustomExceptions("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserCustomExceptions("Email is already in use");
        }
        if (!userType.equals("CUSTOMER") && !userType.equals("SELLER")) {
            throw new UserCustomExceptions("Invalid user type");
        }
        UserType type;
        try {
            type = UserType.valueOf(userType.toUpperCase());
        } catch (Exception e) {
            throw new UserCustomExceptions("Invalid user type");
        }
        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType(type);
        Users user1 = userRepository.save(user);

        // auto-login and return token
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));
        String token = jwtUtiles
                .generateToken((UserDetails) auth.getPrincipal(), user.getId());
        return new AuthResponse(username, email, userType, token);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Users user = userRepository.findByUsernameOrEmail(loginRequest.getLogin(), loginRequest.getLogin())
                .orElseThrow(
                        () -> {
                            if (loginRequest.getLogin().contains("@")) {
                                return new UserCustomExceptions("Email not found :" + loginRequest.getLogin());
                            } else {
                                return new UserCustomExceptions("Username not found :" + loginRequest.getLogin());
                            }
                        });

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(),
                        loginRequest.getPassword()));
        String token = jwtUtiles
                .generateToken((UserDetails) auth.getPrincipal(), user.getId());
        return new AuthResponse(
                user.getUsername(),
                user.getEmail(),
                user.getUserType().name(),
                token);
    }

}

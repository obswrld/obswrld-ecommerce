package com.observe.observe.services;

import org.springframework.stereotype.Service;

import com.observe.observe.dtos.request.UserRegisterRequest;
import com.observe.observe.dtos.response.UserRegistrationResponse;
import com.observe.observe.models.*;
import com.observe.observe.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRegistrationResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .passwordHash(hashPassword(request.getPassword()))
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .role(Role.BUYER)
            .isActive(true)
            .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private UserRegistrationResponse mapToResponse(User user) {
        return UserRegistrationResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
            .isActive(user.isActive())
            .createdAt(user.getCreatedAt())
            .build();
    }

    private String hashPassword(String password) {
        return password;
    }
}
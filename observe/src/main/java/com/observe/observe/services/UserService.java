package com.observe.observe.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

import com.observe.observe.dtos.request.UpdateProfileRequest;
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

    public UserRegistrationResponse getProfile(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapToResponse(user);
    }

    public UserRegistrationResponse updateProfile(UUID id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserRegistrationResponse deActivateAccount(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setActive(false);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }
}
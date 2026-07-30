package com.observe.observe.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

import com.observe.observe.dtos.request.UpdateProfileRequest;
import com.observe.observe.dtos.request.UserRegisterRequest;
import com.observe.observe.dtos.response.UserRegistrationResponse;
import com.observe.observe.mappers.Mapper;
import com.observe.observe.models.*;
import com.observe.observe.repositories.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final Mapper mapper;

    // constructor
    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // this method registers a new user
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

        return mapper.mapToResponse(savedUser);
    }

    // this method returns the user's profile
    public UserRegistrationResponse getProfile(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapper.mapToResponse(user);
    }

    // this method for updating the user's profile
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
        return mapper.mapToResponse(savedUser);
    }

    // this method deactivates the account by setting isActive to false
    public UserRegistrationResponse deActivateAccount(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setActive(false);
        User savedUser = userRepository.save(user);
        return mapper.mapToResponse(savedUser);
    }

    // this method hashes a password
    private String hashPassword(String password) {
        return password;
    }
}
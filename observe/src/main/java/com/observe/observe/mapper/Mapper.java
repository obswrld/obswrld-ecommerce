package com.observe.observe.mapper;

import com.observe.observe.dtos.response.AddressResponse;
import com.observe.observe.dtos.response.UserRegistrationResponse;
import com.observe.observe.models.Address;
import com.observe.observe.models.User;

import org.springframework.stereotype.Component;

@Component
public class Mapper {

    // this method maps a User entity to a UserRegistrationResponse DTO
    public UserRegistrationResponse mapToResponse(User user) {
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

    // this method hashes a password
    public String hashPassword(String password) {
        return password;
    }

    // this method maps an Address entity to an AddressResponse DTO
    public AddressResponse mapToAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipCode(address.getZipCode());
        response.setCountry(address.getCountry());
        response.setIsDefault(address.getIsDefault());
        return response;
    }
}
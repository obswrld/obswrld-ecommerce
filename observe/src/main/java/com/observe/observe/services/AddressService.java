package com.observe.observe.services;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.observe.observe.dtos.request.AddressCreateRequest;
import com.observe.observe.dtos.response.AddressResponse;
import com.observe.observe.models.Address;
import com.observe.observe.models.User;
import com.observe.observe.repositories.AddressRepository;
import com.observe.observe.repositories.UserRepository;
import com.observe.observe.mapper.Mapper;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    // constructor
    public AddressService(AddressRepository addressRepository, UserRepository userRepository, Mapper mapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // this method adds a new address for a user
    public AddressResponse addAddress(UUID userId, AddressCreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserAndIsDefault(user, true)
                .ifPresent(existingDefault -> {
                    existingDefault.setIsDefault(false);
                    addressRepository.save(existingDefault);
                });
        }
        
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setIsDefault(request.getIsDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return mapper.mapToAddressResponse(savedAddress);
    }


    // this method returns all addresses for a user
    public List<AddressResponse> getUserAddress(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream().map(mapper::mapToAddressResponse).toList();
    }

    // this method updates an address by its ID, throwing an exception if the address does not belong to the user
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressCreateRequest request) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to user");
        }
        
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setIsDefault(request.getIsDefault());

        Address savedAddress = addressRepository.save(address);
        return mapper.mapToAddressResponse(savedAddress);
    }

    // this method deletes an address by its ID, throwing an exception if the address does not belong to the user
    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));
            
        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to user");
        }
        addressRepository.delete(address);
    }

}
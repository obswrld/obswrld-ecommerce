package com.observe.observe.services;

import com.observe.observe.dtos.request.AddressCreateRequest;
import com.observe.observe.dtos.response.AddressResponse;
import com.observe.observe.mappers.Mapper;
import com.observe.observe.models.Address;
import com.observe.observe.models.User;
import com.observe.observe.repositories.AddressRepository;
import com.observe.observe.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private AddressCreateRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        request = new AddressCreateRequest(
                "123 Main Street",
                "Lagos",
                "Lagos State",
                "100001",
                "Nigeria",
                false
        );
    }

    private void stubMapperFor(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUser() != null ? address.getUser().getId() : null);
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipCode(address.getZipCode());
        response.setCountry(address.getCountry());
        response.setIsDefault(address.getIsDefault());
        when(mapper.mapToAddressResponse(address)).thenReturn(response);
    }

    // Add Address

    @Test
    void addAddress_shouldSaveAddress_whenNotDefault() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.mapToAddressResponse(any(Address.class))).thenReturn(new AddressResponse());

        AddressResponse response = addressService.addAddress(user.getId(), request);

        verify(addressRepository, never()).findByUserAndIsDefault(any(User.class), anyBoolean());
        verify(addressRepository, times(1)).save(any(Address.class));
        assertNotNull(response);
    }

    @Test
    void addAddress_shouldSaveAsDefault_whenIsDefaultTrueAndNoExistingDefault() {
        AddressCreateRequest defaultRequest = new AddressCreateRequest(
                "123 Main Street", "Lagos", "Lagos State", "100001", "Nigeria", true
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.findByUserAndIsDefault(user, true)).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.mapToAddressResponse(any(Address.class))).thenReturn(new AddressResponse());

        AddressResponse response = addressService.addAddress(user.getId(), defaultRequest);

        verify(addressRepository, times(1)).findByUserAndIsDefault(user, true);
        verify(addressRepository, times(1)).save(any(Address.class));
        assertNotNull(response);
    }

    @Test
    void addAddress_shouldUnsetExistingDefault_whenIsDefaultTrueAndExistingDefaultPresent() {
        AddressCreateRequest defaultRequest = new AddressCreateRequest(
                "123 Main Street", "Lagos", "Lagos State", "100001", "Nigeria", true
        );

        Address existingDefault = new Address();
        existingDefault.setId(UUID.randomUUID());
        existingDefault.setUser(user);
        existingDefault.setIsDefault(true);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.findByUserAndIsDefault(user, true)).thenReturn(Optional.of(existingDefault));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        addressService.addAddress(user.getId(), defaultRequest);

        assertFalse(existingDefault.getIsDefault());
        // one save call for unsetting the old default, one for the new address
        verify(addressRepository, times(2)).save(any(Address.class));
        verify(addressRepository, times(1)).save(existingDefault);
    }

    @Test
    void addAddress_shouldThrowException_whenUserNotFound() {
        UUID missingUserId = UUID.randomUUID();
        when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> addressService.addAddress(missingUserId, request));

        verify(addressRepository, never()).save(any(Address.class));
    }

    // Get User Address

    @Test
    void getUserAddress_shouldReturnMappedList_whenAddressesExist() {
        Address address1 = new Address();
        address1.setId(UUID.randomUUID());
        address1.setUser(user);

        Address address2 = new Address();
        address2.setId(UUID.randomUUID());
        address2.setUser(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(List.of(address1, address2));
        stubMapperFor(address1);
        stubMapperFor(address2);

        List<AddressResponse> responses = addressService.getUserAddress(user.getId());

        assertEquals(2, responses.size());
        verify(mapper, times(1)).mapToAddressResponse(address1);
        verify(mapper, times(1)).mapToAddressResponse(address2);
    }

    @Test
    void getUserAddress_shouldReturnEmptyList_whenUserHasNoAddresses() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(addressRepository.findByUser(user)).thenReturn(List.of());

        List<AddressResponse> responses = addressService.getUserAddress(user.getId());

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void getUserAddress_shouldThrowException_whenUserNotFound() {
        UUID missingUserId = UUID.randomUUID();
        when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> addressService.getUserAddress(missingUserId));
    }

    // Update Address

    @Test
    void updateAddress_shouldUpdateAndReturnResponse_whenAddressBelongsToUser() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUser(user);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        stubMapperFor(address);

        AddressResponse response = addressService.updateAddress(user.getId(), address.getId(), request);

        assertEquals(request.getStreet(), address.getStreet());
        assertEquals(request.getCity(), address.getCity());
        verify(addressRepository, times(1)).save(address);
        assertNotNull(response);
    }

    @Test
    void updateAddress_shouldThrowException_whenAddressNotFound() {
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> addressService.updateAddress(user.getId(), addressId, request));

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateAddress_shouldThrowException_whenAddressBelongsToDifferentUser() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUser(otherUser);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        assertThrows(IllegalArgumentException.class,
                () -> addressService.updateAddress(user.getId(), address.getId(), request));

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void updateAddress_shouldUnsetDefault_whenSettingNewDefault() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUser(user);
        address.setIsDefault(false);

        Address previousDefault = new Address();
        previousDefault.setId(UUID.randomUUID());
        previousDefault.setUser(user);
        previousDefault.setIsDefault(true);

        AddressCreateRequest request = new AddressCreateRequest(
                "123 Main Street", 
                "Yaba",
                "Lagos",
                "100001",
                "Nigeria",
                true
        );

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(address));
        when(addressRepository.findByUserAndIsDefault(any(User.class), eq(true))).thenReturn(Optional.of(previousDefault));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        stubMapperFor(address);

        addressService.updateAddress(user.getId(), address.getId(), request);

        assertFalse(previousDefault.getIsDefault());
        verify(addressRepository, times(1)).save(previousDefault);
        verify(addressRepository, times(1)).save(address);
        
    }

    // Delete Address

    @Test
    void deleteAddress_shouldDeleteAddress_whenBelongsToUser() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUser(user);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        addressService.deleteAddress(user.getId(), address.getId());

        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void deleteAddress_shouldThrowException_whenAddressNotFound() {
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> addressService.deleteAddress(user.getId(), addressId));

        verify(addressRepository, never()).delete(any(Address.class));
    }

    @Test
    void deleteAddress_shouldThrowException_whenAddressBelongsToDifferentUser() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setUser(otherUser);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));

        assertThrows(IllegalArgumentException.class,
                () -> addressService.deleteAddress(user.getId(), address.getId()));

        verify(addressRepository, never()).delete(any(Address.class));
    }
}
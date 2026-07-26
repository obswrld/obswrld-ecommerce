package com.observe.observe.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import com.observe.observe.dtos.response.UserRegistrationResponse;
import com.observe.observe.dtos.request.UserRegisterRequest;
import com.observe.observe.models.*;
import com.observe.observe.repositories.*;
import java.util.Optional;
import com.observe.observe.dtos.request.UpdateProfileRequest;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    
    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldSaveUserWhenEmailAndUsernameAreAvailable() {
        UserRegisterRequest request = new UserRegisterRequest(
            "republicoba1@gmail.com",
            "password123",
            "obswrld__",
            "Oba Republic",
            "07025266994"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        UserRegistrationResponse response = userService.register(request);

        assertNotNull(response.getId());
        assertEquals("republicoba1@gmail.com", response.getEmail());
        assertEquals("Oba Republic", response.getFullName());
        assertEquals(Role.BUYER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest(
            "republicoba1@gmail.com",
            "password123",
            "obswrld__",
            "Oba Republic",
            "07025266994"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenUsernameAlreadyExists() {
        UserRegisterRequest request = new UserRegisterRequest(
            "republicoba1@gmail.com",
            "password123",
            "obswrld__",
            "Oba Republic",
            "07025266994"
        );

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getProfile_ShouldReturnUserProfileWhenUserExists() {

        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("republicoba1@gmail.com");
        user.setUsername("obswrld__");
        user.setFullName("Oba Republic");
        user.setPhoneNumber("07025266994");
        user.setRole(Role.BUYER);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // Act
        UserRegistrationResponse response = userService.getProfile(UUID.randomUUID());

        // Assert
        assertNotNull(response.getId());
        assertEquals("republicoba1@gmail.com", response.getEmail());
        assertEquals("obswrld__", response.getUsername());
        assertEquals("Oba Republic", response.getFullName());
        assertEquals("07025266994", response.getPhoneNumber());
        assertEquals(Role.BUYER, response.getRole());
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getProfile_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getProfile(UUID.randomUUID()));
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void updateProfile_ShouldUpdateUserProfileWhenUserExists() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("republicoba1@gmail.com");
        user.setUsername("obswrld__");
        user.setFullName("Oba Republic");
        user.setPhoneNumber("07025266994");
        user.setRole(Role.BUYER);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRegistrationResponse response = userService.updateProfile(UUID.randomUUID(), new UpdateProfileRequest(
            "obswrld110!",
            "Oba Republic",
            "07025266994"
        ));

        assertNotNull(response.getId());
        assertEquals("republicoba1@gmail.com", response.getEmail());
        assertEquals("obswrld110!", response.getUsername());
        assertEquals("Oba Republic", response.getFullName());
        assertEquals("07025266994", response.getPhoneNumber());
        assertEquals(Role.BUYER, response.getRole());
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(UUID.randomUUID(), new UpdateProfileRequest(
            "obswrld110!",
            "Oba Republic",
            "07025266994"
        )));
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deActivateAccount_ShouldDeActivateAccountWhenUserExists() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("republicoba1@gmail.com");
        user.setUsername("obswrld__");
        user.setFullName("Oba Republic");
        user.setPhoneNumber("07025266994");
        user.setRole(Role.BUYER);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRegistrationResponse response = userService.deActivateAccount(UUID.randomUUID());

        assertNotNull(response.getId());
        assertEquals("republicoba1@gmail.com", response.getEmail());
        assertEquals("obswrld__", response.getUsername());
        assertEquals("Oba Republic", response.getFullName());
        assertEquals("07025266994", response.getPhoneNumber());
        assertEquals(Role.BUYER, response.getRole());
        assertFalse(response.isActive());
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deActivateAccount_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deActivateAccount(UUID.randomUUID()));
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    
}
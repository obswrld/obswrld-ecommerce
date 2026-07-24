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

    
}
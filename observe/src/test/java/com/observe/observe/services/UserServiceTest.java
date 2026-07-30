package com.observe.observe.services;

import com.observe.observe.dtos.request.UpdateProfileRequest;
import com.observe.observe.dtos.request.UserRegisterRequest;
import com.observe.observe.dtos.response.UserRegistrationResponse;
import com.observe.observe.mappers.Mapper;
import com.observe.observe.models.Role;
import com.observe.observe.models.User;
import com.observe.observe.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest registerRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerRequest = new UserRegisterRequest(
                "republicoba1@gmail.com",
                "password123",
                "obswrld__",
                "Oba Republic",
                "07025266994"
        );

        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("republicoba1@gmail.com");
        existingUser.setUsername("obswrld__");
        existingUser.setFullName("Oba Republic");
        existingUser.setPhoneNumber("07025266994");
        existingUser.setRole(Role.BUYER);
        existingUser.setActive(true);
        existingUser.setCreatedAt(LocalDateTime.now());
    }

    private void stubMapperFor(User user) {
        when(mapper.mapToResponse(user)).thenReturn(
                UserRegistrationResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .isActive(user.isActive())
                        .createdAt(user.getCreatedAt())
                        .build()
        );
    }

    // Registration 

    @Test
    void register_shouldSaveUserAndReturnResponse_whenEmailAndUsernameAreAvailable() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        when(mapper.mapToResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserRegistrationResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .isActive(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .build();
        });

        UserRegistrationResponse response = userService.register(registerRequest);

        assertNotNull(response.getId());
        assertEquals(registerRequest.getEmail(), response.getEmail());
        assertEquals(Role.BUYER, response.getRole());
        assertTrue(response.isActive());

        // hashPassword is currently a no-op passthrough, so passwordHash should equal the raw request password.
        // Once real hashing is implemented, this assertion must change to assert inequality + verify against a hasher.
        User savedUser = userCaptor.getValue();
        assertEquals(registerRequest.getPassword(), savedUser.getPasswordHash());
        assertEquals(Role.BUYER, savedUser.getRole());
        assertTrue(savedUser.isActive());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequest));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldCallMapperWithSavedUser_notPreSaveObject() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        UUID generatedId = UUID.randomUUID();
        LocalDateTime generatedCreatedAt = LocalDateTime.now();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(generatedId);
            user.setCreatedAt(generatedCreatedAt);
            return user;
        });

        ArgumentCaptor<User> mapperCaptor = ArgumentCaptor.forClass(User.class);
        when(mapper.mapToResponse(mapperCaptor.capture()))
                .thenReturn(UserRegistrationResponse.builder().id(generatedId).build());

        userService.register(registerRequest);

        User capturedUser = mapperCaptor.getValue();
        assertEquals(generatedId, capturedUser.getId());
        assertEquals(generatedCreatedAt, capturedUser.getCreatedAt());
    }

    // Get Profile

    @Test
    void getProfile_shouldReturnMappedResponse_whenUserExists() {
        UUID id = existingUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        stubMapperFor(existingUser);

        UserRegistrationResponse response = userService.getProfile(id);

        assertEquals(existingUser.getId(), response.getId());
        assertEquals(existingUser.getEmail(), response.getEmail());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getProfile_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.getProfile(null));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getProfile_shouldThrowException_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getProfile(id));
    }

    // Update Profile

    @Test
    void updateProfile_shouldUpdateAllFields_whenAllProvidedAndValid() {
        UUID id = existingUser.getId();
        UpdateProfileRequest request = new UpdateProfileRequest("newUsername", "New Name", "08000000000");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        UserRegistrationResponse response = userService.updateProfile(id, request);

        assertEquals("newUsername", existingUser.getUsername());
        assertEquals("New Name", existingUser.getFullName());
        assertEquals("08000000000", existingUser.getPhoneNumber());
        verify(userRepository, times(1)).save(existingUser);
        assertNotNull(response);
    }

    @Test
    void updateProfile_shouldOnlyUpdateFullName_whenOnlyFullNameProvided() {
        UUID id = existingUser.getId();
        String originalUsername = existingUser.getUsername();
        String originalPhone = existingUser.getPhoneNumber();

        UpdateProfileRequest request = new UpdateProfileRequest(null, "Updated Name Only", null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        userService.updateProfile(id, request);

        assertEquals("Updated Name Only", existingUser.getFullName());
        assertEquals(originalUsername, existingUser.getUsername());
        assertEquals(originalPhone, existingUser.getPhoneNumber());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void updateProfile_shouldSkipUniquenessCheck_whenUsernameUnchanged() {
        UUID id = existingUser.getId();
        UpdateProfileRequest request = new UpdateProfileRequest(existingUser.getUsername(), null, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        userService.updateProfile(id, request);

        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void updateProfile_shouldThrowException_whenNewUsernameAlreadyTaken() {
        UUID id = existingUser.getId();
        UpdateProfileRequest request = new UpdateProfileRequest("takenUsername", "Some Name", "08000000000");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("takenUsername")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(id, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_shouldThrowException_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        UpdateProfileRequest request = new UpdateProfileRequest(null, "Name", null);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(id, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_shouldStillSaveAndReturn_whenAllFieldsNull() {
        UUID id = existingUser.getId();
        UpdateProfileRequest request = new UpdateProfileRequest(null, null, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        UserRegistrationResponse response = userService.updateProfile(id, request);

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, times(1)).save(existingUser);
        assertNotNull(response);
    }

    // Deactivate Account

    @Test
    void deActivateAccount_shouldSetInactiveAndReturnResponse_whenUserExists() {
        UUID id = existingUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        userService.deActivateAccount(id);

        assertFalse(existingUser.isActive());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deActivateAccount_shouldThrowException_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deActivateAccount(id));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deActivateAccount_shouldSucceed_evenIfAlreadyInactive() {
        existingUser.setActive(false);
        UUID id = existingUser.getId();
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        stubMapperFor(existingUser);

        // No guard against double-deactivation currently exists in UserService.
        // This test documents current behavior; whether that's the intended contract is a design decision to revisit.
        assertDoesNotThrow(() -> userService.deActivateAccount(id));
        assertFalse(existingUser.isActive());
    }
}
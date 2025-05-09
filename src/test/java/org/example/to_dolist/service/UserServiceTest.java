package org.example.to_dolist.service;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.exception.UserNotFoundException;
import org.example.to_dolist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("testName"); // ✅ Javítva setUsername -> setName
    }

    // ===================== getUserById =====================

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(testUserId);

        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(testUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with ID");

        verify(userRepository, times(1)).findById(testUserId);
    }

    // ===================== getAllUsers =====================

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setName("anotherName"); // ✅ Javítva setUsername -> setName

        List<User> userList = List.of(testUser, anotherUser);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2).contains(testUser, anotherUser);
        verify(userRepository, times(1)).findAll();
    }

    // ===================== save =====================

    @Test
    void save_ShouldReturnSavedUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.save(testUser);

        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).save(testUser);
    }

    // ===================== deleteById =====================

    @Test
    void deleteById_ShouldCallDelete_WhenUserExists() {
        when(userRepository.existsById(testUserId)).thenReturn(true);

        userService.deleteById(testUserId);

        verify(userRepository, times(1)).deleteById(testUserId);
        verify(userRepository, times(1)).existsById(testUserId);
    }

    @Test
    void deleteById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(testUserId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(testUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot delete. User not found with ID");

        verify(userRepository, never()).deleteById(any());
        verify(userRepository, times(1)).existsById(testUserId);
    }
}
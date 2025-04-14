package com.example.todolist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.todolist.domain.User;
import com.example.todolist.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserService underTest;

    @Test
    void getAllUsers() {
        List<User> expectedUsers = List.of(
                User.builder()
                        .id(UUID.randomUUID())
                        .name("Alice")
                        .build(),
                User.builder()
                        .id(UUID.randomUUID())
                        .name("Bob")
                        .build()
        );

        when(userRepositoryMock.findAll()).thenReturn(expectedUsers);

        List<User> result = underTest.getAllUsers();

        assertEquals(expectedUsers, result);
    }

    @Test
    void saveHappyPath() {
        User expectedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Charlie")
                .build();

        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);

        User result = underTest.save(expectedUser);

        assertEquals(expectedUser, result);
    }

    @Test
    void editHappyPath() {
        User updatedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Updated Charlie")
                .build();

        when(userRepositoryMock.save(updatedUser)).thenReturn(updatedUser);

        User result = underTest.save(updatedUser);

        assertEquals(updatedUser, result);
    }
}

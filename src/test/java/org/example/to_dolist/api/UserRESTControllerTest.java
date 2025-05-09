package org.example.to_dolist.api;

import org.example.to_dolist.controller.api.UserRESTController;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserRESTControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRESTController userRESTController;

    private final UUID testUserId = UUID.randomUUID();
    private final User testUser = User.builder()
            .id(testUserId)
            .name("Test User")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRESTController).build();
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testUserId.toString()))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(testUserId)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Test User",
                            "dateOfBirth": "1990-01-01"
                        }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId.toString()));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "id": "%s",
                            "name": "Updated User",
                            "dateOfBirth": "1990-01-01"
                        }""".formatted(testUserId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUserId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(testUserId);
    }
}
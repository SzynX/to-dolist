package org.example.to_dolist.controller;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private Model model;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        model = mock(Model.class);
        userController = new UserController(userService);
    }

    @Test
    void getAllUsers_shouldAddUsersToModelAndReturnView() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = userController.getAllUsers(model);

        verify(model).addAttribute(eq("users"), eq(Collections.emptyList()));
        assertEquals("users/users", viewName);
    }

    @Test
    void createUserForm_shouldReturnCreateUserViewWithEmptyUser() {
        String viewName = userController.createUserForm(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("users/create-user", viewName);
    }

    @Test
    void saveUser_shouldCallServiceAndRedirect() {
        User user = User.builder().build();

        String result = userController.saveUser(user);

        verify(userService).save(user);
        assertEquals("redirect:/users", result);
    }

    @Test
    void editUserForm_shouldAddUserToModelAndReturnEditView() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        when(userService.getUserById(userId)).thenReturn(user);

        String viewName = userController.editUserForm(userId, model);

        verify(model).addAttribute("user", user);
        assertEquals("users/edit-user", viewName);
    }

    @Test
    void updateUser_shouldSaveUserAndRedirect() {
        User user = User.builder().build();

        String result = userController.updateUser(user);

        verify(userService).save(user);
        assertEquals("redirect:/users", result);
    }

    @Test
    void deleteUser_shouldCallDeleteAndRedirect() {
        UUID id = UUID.randomUUID();

        String result = userController.deleteUser(id);

        verify(userService).deleteById(id);
        assertEquals("redirect:/users", result);
    }
}

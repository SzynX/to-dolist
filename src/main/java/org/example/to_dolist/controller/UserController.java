package org.example.to_dolist.controller;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userServiceParam) {
        this.userService = userServiceParam;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/users";
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", User.builder().build());
        return "users/create-user";
    }

    @PostMapping
    public String saveUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "users/edit-user";
    }

    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
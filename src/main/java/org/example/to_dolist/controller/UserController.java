package org.example.to_dolist.controller;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET: List all users
    @GetMapping("/list")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/users";
    }

    // GET: Show Create User Page
    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create-user";
    }

    // POST: Save New User
    @PostMapping
    public String saveUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users/list";
    }

    // GET: Show Edit User Page
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable UUID id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/edit-user";
    }

    // POST: Update Existing User
    @PostMapping("/edit")
    public String updateUser(@ModelAttribute User user) {
        userService.edit(user);
        return "redirect:/users/list";
    }

    // POST: Delete User
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
        return "redirect:/users/list";
    }
}

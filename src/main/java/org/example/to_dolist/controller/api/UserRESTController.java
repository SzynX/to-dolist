package org.example.to_dolist.controller.api;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/users") // http://localhost:8080/api/users
public class UserRESTController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.edit(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
    }
}

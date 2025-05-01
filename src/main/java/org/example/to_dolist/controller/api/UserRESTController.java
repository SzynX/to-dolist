package org.example.to_dolist.controller.api;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users") // http://localhost:8080/api/users
public class UserRESTController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        // A UserService-ben a findById metódus átnevezésre/egyesítésre került getUserById néven
        return userService.getUserById(id);
    }

    @PostMapping
    // Általában a create/update endpointokat célszerű azonos URL-re tenni
    // A POST /api/users -> create
    // A PUT /api/users/{id} -> update
    // De a jelenlegi kódodhoz illeszkedve marad a /create és /update
    @RequestMapping(value = "/create", method = RequestMethod.POST) // Explicit módon megadva a metódus és az útvonal
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping
    @RequestMapping(value = "/update", method = RequestMethod.POST) // Explicit módon megadva a metódus és az útvonal
    public User updateUser(@RequestBody User user) {
        // A UserService-ben az edit metódus átnevezésre/egyesítésre került save néven
        // A save metódus a JPA repository save metódusát hívja, ami ID alapján eldönti, hogy insert vagy update
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteById(id);
    }
}
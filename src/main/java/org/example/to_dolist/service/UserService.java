package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User edit(User user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}

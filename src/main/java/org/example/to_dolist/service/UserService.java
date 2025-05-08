package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.exception.UserNotFoundException;
import org.example.to_dolist.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Lazy inicializáció aktiválása (ha szükséges a tasks lista eléréséhez itt vagy a nézetben)
        // users.forEach(user -> Hibernate.initialize(user.getTasks())); // Ezt a sort csak akkor kell, ha users.getTasks() hívás történik itt vagy a nézetben
        return users;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
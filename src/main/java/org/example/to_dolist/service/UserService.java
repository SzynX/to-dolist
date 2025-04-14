package org.example.to_dolist.service;

import org.example.to_dolist.domain.User;
import org.example.to_dolist.exception.NoSuchEntityException;
import org.example.to_dolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User edit(User user) {
        return userRepository.save(user);
    }

    public User findById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NoSuchEntityException("There was no user with id: " + id);
        }
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}

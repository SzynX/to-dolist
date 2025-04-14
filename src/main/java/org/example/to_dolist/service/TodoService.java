package org.example.to_dolist.service;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.exception.NoSuchEntityException;
import org.example.to_dolist.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo edit(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo findById(UUID id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            return optionalTodo.get();
        } else {
            throw new NoSuchEntityException("There was no todo found for this id: " + id);
        }
    }

    public void deleteById(UUID id) {
        todoRepository.deleteById(id);
    }
}

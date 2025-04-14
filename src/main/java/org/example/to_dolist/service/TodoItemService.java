package org.example.to_dolist.service;

import org.example.to_dolist.model.TodoItem;
import org.example.to_dolist.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;

    @Autowired
    public TodoItemService(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    public Optional<TodoItem> getTodoById(Long id) {
        return todoItemRepository.findById(id);
    }

    public TodoItem createTodo(TodoItem todoItem) {
        return todoItemRepository.save(todoItem);
    }

    public TodoItem updateTodo(Long id, TodoItem todoDetails) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoItem.setTask(todoDetails.getTask());
        todoItem.setCompleted(todoDetails.isCompleted());
        return todoItemRepository.save(todoItem);
    }

    public void deleteTodo(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoItemRepository.delete(todoItem);
    }
}
package org.example.to_dolist.service; // Ellenőrizd a csomagnevet!

import org.example.to_dolist.exception.ResourceNotFoundException;
import org.example.to_dolist.model.TodoItem;
import org.example.to_dolist.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Jelzi Spring számára, hogy ez egy Service bean
public class TodoItemService {

    @Autowired // Spring beinjektálja a Repository példányt
    private TodoItemRepository todoItemRepository;

    public List<TodoItem> getAllTodos() {
        return todoItemRepository.findAll();
    }

    public Optional<TodoItem> getTodoById(Long id) {
        // Közvetlenül visszaadhatjuk az Optional-t, a hívó majd eldönti, mit kezd vele
        return todoItemRepository.findById(id);
    }

    public TodoItem createTodo(TodoItem todoItem) {
        // Itt lehetne validáció vagy alapértelmezett értékek beállítása
        todoItem.setCompleted(false); // Biztosítjuk, hogy új elem ne legyen kész
        return todoItemRepository.save(todoItem);
    }

    public TodoItem updateTodo(Long id, TodoItem todoDetails) {
        // Megkeressük a létező elemet, vagy hibát dobunk, ha nincs meg
        TodoItem existingTodo = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id: " + id));

        // Frissítjük a mezőket
        existingTodo.setTask(todoDetails.getTask());
        existingTodo.setCompleted(todoDetails.isCompleted());
        // TODO: Később itt lehetne más mezőket is frissíteni

        // Elmentjük a frissített elemet
        return todoItemRepository.save(existingTodo);
    }

    public void deleteTodo(Long id) {
        // Megkeressük a létező elemet, vagy hibát dobunk, ha nincs meg
        TodoItem existingTodo = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id: " + id));
        // Töröljük az elemet
        todoItemRepository.delete(existingTodo);
    }
}
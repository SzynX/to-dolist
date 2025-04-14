package org.example.to_dolist.controller.api;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/todos")
public class TodoRESTController {

    @Autowired
    private TodoService todoService;

    @GetMapping()
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable UUID id) {
        return todoService.findById(id);
    }

    @PostMapping("/create")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.save(todo);
    }

    @PostMapping("/update")
    public Todo updateTodo(@RequestBody Todo todo) {
        return todoService.edit(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodoById(@PathVariable UUID id) {
        todoService.deleteById(id);
    }
}

package org.example.to_dolist.controller.api;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
public class TodoRESTController {

    @Autowired
    private TodoService todoService;


    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable UUID id) {
        Todo todo = todoService.findById(id);
        return todo != null
                ? new ResponseEntity<>(todo, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo createdTodo = todoService.save(todo);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(
            @PathVariable UUID id,
            @RequestBody Todo todo
    ) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo != null) {
            todo.setId(id);
            Todo updatedTodo = todoService.edit(todo);
            return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable UUID id) {
        Todo todo = todoService.findById(id);
        if (todo != null) {
            todoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
package org.example.to_dolist.controller;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.service.TodoService;
import org.example.to_dolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    // ✅ Konstruktoros injekció
    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String getAllTodos(Model model) {
        List<Todo> todos = todoService.getAllTodos();
        model.addAttribute("todos", todos);
        return "todos/todos";
    }

    @GetMapping("/new")
    public String createTodoForm(Model model) {
        model.addAttribute("todo", new Todo());
        model.addAttribute("users", userService.getAllUsers());
        return "todos/create-todo";
    }

    @PostMapping
    public String saveTodo(@ModelAttribute Todo todo) {
        todoService.save(todo);
        return "redirect:/todos/list";
    }

    @GetMapping("/edit/{id}")
    public String editTodoForm(@PathVariable UUID id, Model model) {
        Todo todo = todoService.findById(id);
        model.addAttribute("todo", todo);
        model.addAttribute("users", userService.getAllUsers());
        return "todos/edit-todo";
    }

    @PostMapping("/edit")
    public String updateTodo(@ModelAttribute Todo todo) {
        todoService.edit(todo);
        return "redirect:/todos/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable UUID id) {
        todoService.deleteById(id);
        return "redirect:/todos/list";
    }

    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        List<Todo> tasks = todoService.getAllTodos();
        model.addAttribute("tasks", tasks);
        return "tasks/tasks";
    }
}

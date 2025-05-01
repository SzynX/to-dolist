package org.example.to_dolist.controller;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.service.TodoService;
import org.example.to_dolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/todos") // Ha szükséges, ezt módosíthatod a /tasks-ra is
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    // GET: List all todos (ez most a /todos/list)
    @GetMapping("/list")
    public String getAllTodos(Model model) {
        List<Todo> todos = todoService.getAllTodos();
        model.addAttribute("todos", todos);
        return "todos/todos"; // Thymeleaf template in templates/todos/todos.html
    }

    // GET: Show Create Todo Page
    @GetMapping("/new")
    public String createTodoForm(Model model) {
        model.addAttribute("todo", new Todo());
        model.addAttribute("users", userService.getAllUsers());
        return "todos/create-todo"; // Template for creating a new Todo
    }

    // POST: Save New Todo
    @PostMapping
    public String saveTodo(@ModelAttribute Todo todo) {
        todoService.save(todo);
        return "redirect:/todos/list";
    }

    // GET: Show Edit Todo Page
    @GetMapping("/edit/{id}")
    public String editTodoForm(@PathVariable UUID id, Model model) {
        Todo todo = todoService.findById(id);
        model.addAttribute("todo", todo);
        model.addAttribute("users", userService.getAllUsers());
        return "todos/edit-todo"; // Edit page for Todo
    }

    // POST: Update Existing Todo
    @PostMapping("/edit")
    public String updateTodo(@ModelAttribute Todo todo) {
        todoService.edit(todo);
        return "redirect:/todos/list";
    }

    // POST: Delete Todo
    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable UUID id) {
        todoService.deleteById(id);
        return "redirect:/todos/list";
    }

    // -----------------------------------------
    // NEW: GET: Show Task List Page (for /tasks URL)
    @GetMapping("/tasks") // For /tasks list
    public String getAllTasks(Model model) {
        List<Todo> tasks = todoService.getAllTodos(); // Assuming tasks and todos are same
        model.addAttribute("tasks", tasks);
        return "tasks/tasks"; // This should correspond to a Thymeleaf template tasks/tasks.html
    }
}
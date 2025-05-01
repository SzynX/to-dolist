package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.TaskStatus;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks/tasks";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("users", userService.getAllUsers());
        return "tasks/create-task";
    }

    @PostMapping
    public String saveTask(@ModelAttribute Task task) {
        if (task.getUser() != null && task.getUser().getId() != null) {
            User user = userService.getUserById(task.getUser().getId());
            task.setUser(user);
        }
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task.getUser() == null) {
            task.setUser(new User());
        }
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("users", userService.getAllUsers());
        return "tasks/edit-task";
    }

    @PostMapping("/edit")
    public String updateTask(@ModelAttribute Task task) {
        if (task.getUser() != null && task.getUser().getId() != null) {
            User user = userService.getUserById(task.getUser().getId());
            task.setUser(user);
        }
        taskService.updateTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}

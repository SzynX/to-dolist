package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.TaskStatus;
import org.example.to_dolist.domain.TaskPriority;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
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
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("users", userService.getAllUsers());
        return "tasks/create-task";
    }

    @PostMapping
    public String saveTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam("userId") UUID userId,
            @RequestParam("priority") TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            return "tasks/create-task";
        }

        try {
            task.setPriority(priority);
            User user = userService.getUserById(userId);
            user.addTask(task);
            taskService.createTask(task);
            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("users", userService.getAllUsers());
        return "tasks/edit-task";
    }

    @PostMapping("/edit")
    public String updateTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam("userId") UUID userId,
            @RequestParam("priority") TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            return "tasks/edit-task";
        }

        try {
            task.setPriority(priority);
            User user = userService.getUserById(userId);
            task.setUser(user);
            taskService.updateTask(task);
            redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting task: " + e.getMessage());
        }
        return "redirect:/tasks";
    }

    // Végpont neve: /due-warning (egyezzen a linkkel)
    @GetMapping("/due-warning")
    public String getTasksWithDueDateWarnings(Model model) {
        List<Task> tasksWithDueWarnings = taskService.getTasksWithDueDateWarnings();
        model.addAttribute("tasksWithDueWarnings", tasksWithDueWarnings);
        return "tasks/due-warning";  // A HTML fájl neve: due-warning.html
    }
}
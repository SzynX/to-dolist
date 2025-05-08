package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.TaskStatus;
import org.example.to_dolist.domain.TaskPriority;  // Importáljuk a TaskPriority-t
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String listTasks(Model model) {
        // Az összes feladat lekérése
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks/tasks"; // A "tasks/tasks" sablon betöltése
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new Task()); // Új task objektum létrehozása
        model.addAttribute("statuses", TaskStatus.values()); // Feladat állapotok betöltése
        model.addAttribute("priorities", TaskPriority.values()); // Prioritások betöltése
        model.addAttribute("users", userService.getAllUsers()); // Felhasználók betöltése
        return "tasks/create-task"; // A "tasks/create-task" sablon betöltése
    }

    @PostMapping
    public String saveTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam("userId") UUID userId,
            @RequestParam("priority") TaskPriority priority,  // Prioritás paraméter hozzáadása
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());  // Újra hozzáadjuk a prioritásokat
            model.addAttribute("users", userService.getAllUsers());
            return "tasks/create-task"; // Ha hiba van, visszatérünk a form-hoz
        }

        try {
            task.setPriority(priority);  // Prioritás beállítása
            User user = userService.getUserById(userId); // Felhasználó lekérése
            user.addTask(task); // Feladat hozzáadása a felhasználóhoz
            taskService.createTask(task); // Feladat mentése az adatbázisba
            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
        }
        return "redirect:/tasks"; // A feladat sikeres mentése után visszairányítunk a feladatok listájára
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id); // A feladat lekérése ID alapján
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values()); // Állapotok betöltése
        model.addAttribute("priorities", TaskPriority.values()); // Prioritások betöltése
        model.addAttribute("users", userService.getAllUsers()); // Felhasználók betöltése
        return "tasks/edit-task"; // A "tasks/edit-task" sablon betöltése
    }

    @PostMapping("/edit")
    public String updateTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam("userId") UUID userId,
            @RequestParam("priority") TaskPriority priority,  // Prioritás paraméter hozzáadása
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());  // Újra hozzáadjuk a prioritásokat
            model.addAttribute("users", userService.getAllUsers());
            return "tasks/edit-task"; // Ha hiba van, visszatérünk a form-hoz
        }

        try {
            task.setPriority(priority);  // Prioritás beállítása
            User user = userService.getUserById(userId); // Felhasználó lekérése
            task.setUser(user); // A feladat felhasználójának beállítása
            taskService.updateTask(task); // A feladat frissítése
            redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
        }
        return "redirect:/tasks"; // A feladat sikeres frissítése után visszairányítunk a feladatok listájára
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taskService.deleteTask(id); // A feladat törlése
            redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting task: " + e.getMessage());
        }
        return "redirect:/tasks"; // A feladat sikeres törlése után visszairányítunk a feladatok listájára
    }
}

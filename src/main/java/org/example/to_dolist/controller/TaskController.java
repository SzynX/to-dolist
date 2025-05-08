package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.TaskStatus;
import org.example.to_dolist.domain.TaskPriority;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.http.ResponseEntity; // Importálni
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
        return "tasks/tasks"; // A HTML sablon neve
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("users", userService.getAllUsers());
        return "tasks/create-task"; // A HTML sablon neve
    }

    @PostMapping
    public String saveTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam(value = "userId", required = false) UUID userId, // userId lehet null, ha nincs kiválasztva
            @RequestParam("priority") TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            // Hozzáadunk egy hibajelzést a Modelhez, ha van binding hiba
            model.addAttribute("error", "Validation errors occurred.");
            return "tasks/create-task";
        }

        try {
            task.setPriority(priority);
            // Felhasználó hozzárendelése, ha van userId
            if (userId != null) {
                User user = userService.getUserById(userId);
                task.setUser(user); // Task entitásban már van user mező
            } else {
                task.setUser(null); // Nincs felhasználó hozzárendelve
            }

            // Új feladat létrehozásakor a "completed" mezőt a service-ben állítjuk be
            taskService.createTask(task); // Itt már nem hívjuk meg az addTask-ot a User-en

            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
            // Logoljuk a hibát éles környezetben!
            e.printStackTrace();
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
        return "tasks/edit-task"; // A HTML sablon neve
    }

    @PostMapping("/edit")
    public String updateTask(
            @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            @RequestParam(value = "userId", required = false) UUID userId, // userId lehet null, ha nincs kiválasztva
            @RequestParam("priority") TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // A BindingResult ellenőrzése előtt mentsük az id-t, ha van,
        // hogy hiba esetén az űrlap újra megjelenhessen a helyes id-val
        UUID taskId = task.getId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("task", taskService.getTaskById(taskId)); // Töltse vissza a feladatot az ID alapján
            model.addAttribute("error", "Validation errors occurred.");
            return "tasks/edit-task";
        }

        try {
            task.setPriority(priority);
            // Felhasználó hozzárendelése, ha van userId
            if (userId != null) {
                User user = userService.getUserById(userId);
                task.setUser(user);
            } else {
                task.setUser(null); // Nincs felhasználó hozzárendelve
            }

            taskService.updateTask(task);
            redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
            e.printStackTrace(); // Logolás
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
            e.printStackTrace(); // Logolás
        }
        return "redirect:/tasks";
    }

    @GetMapping("/due-warning")
    public String getTasksWithDueDateWarnings(Model model) {
        List<Task> tasksWithDueWarnings = taskService.getTasksWithDueDateWarnings();
        model.addAttribute("tasksWithDueWarnings", tasksWithDueWarnings);
        return "tasks/due-warning"; // A HTML sablon neve
    }

    // ÚJ VÉGPONT a completed státusz váltásához AJAX hívásra
    @PostMapping("/toggle-completed/{id}")
    @ResponseBody // Visszatérésként nem nézetet, hanem adatot küldünk (pl. JSON vagy egyszerű státusz)
    public ResponseEntity<?> toggleCompleted(@PathVariable UUID id) {
        try {
            taskService.toggleTaskCompleted(id);
            return ResponseEntity.ok().build(); // Siker esetén 200 OK státusz
        } catch (Exception e) {
            // Hiba esetén 500 Internal Server Error státusz
            return ResponseEntity.internalServerError().body("Error toggling task completed status: " + e.getMessage());
        }
    }
}
package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskStatus;
import org.example.to_dolist.domain.Task.TaskPriority;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity; // Ez most már nem kell a toggle végpontnál
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

    // Fő feladatlista végpont - csak NEM archivált feladatokat listáz
    @GetMapping
    public String listTasks(
            Model model,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        // getAllTasks mostantól csak a NEM archiváltakat adja vissza
        model.addAttribute("tasks", taskService.getAllTasks(userId, sort));

        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDirection", sortDirection);

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUserId", userId);

        model.addAttribute("isArchiveView", false); // Jelző a nézetnek, hogy nem archív
        return "tasks/tasks";
    }

    // ÚJ VÉGPONT az archivált feladatok listázásához
    @GetMapping("/archive")
    public String listArchivedTasks(
            Model model,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(value = "userId", required = false) String userId
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        // getAllArchivedTasks lekéri az archiváltakat
        model.addAttribute("tasks", taskService.getAllArchivedTasks(userId, sort));

        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDirection", sortDirection);

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUserId", userId);

        model.addAttribute("isArchiveView", true); // Jelző a nézetnek, hogy ez az archív lista
        return "tasks/archive"; // Az új HTML sablon neve az archív listához
    }


    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "tasks/view-task";
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
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam("priority") Task.TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("error", "Validation errors occurred.");
            return "tasks/create-task";
        }

        try {
            task.setPriority(priority);
            if (userId != null) {
                User user = userService.getUserById(userId);
                task.setUser(user);
            } else {
                task.setUser(null);
            }
            taskService.createTask(task); // createTask beállítja archived=false
            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating task: " + e.getMessage());
            e.printStackTrace(); // Loggoljuk a hibát a szerver oldalon
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
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam("priority") Task.TaskPriority priority,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UUID taskId = task.getId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("task", taskService.getTaskById(taskId));
            model.addAttribute("error", "Validation errors occurred.");
            return "tasks/edit-task";
        }

        try {
            task.setPriority(priority);
            if (userId != null) {
                User user = userService.getUserById(userId);
                task.setUser(user);
            } else {
                task.setUser(null);
            }
            taskService.updateTask(task);
            redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating task: " + e.getMessage());
            e.printStackTrace(); // Loggoljuk a hibát
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
            e.printStackTrace(); // Loggoljuk a hibát
        }
        // Visszairányítás attól függően, hol voltunk (főlista vagy archív)
        // Ezt a böngésző referrer fejléce alapján lehetne megállapítani, vagy egy rejtett mezővel
        // Most maradjunk a főlistánál
        return "redirect:/tasks";
    }

    // ÚJ VÉGPONT feladat archiválásához
    @PostMapping("/archive/{id}")
    public String archiveTask(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taskService.archiveTask(id);
            redirectAttributes.addFlashAttribute("success", "Task archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error archiving task: " + e.getMessage());
            e.printStackTrace(); // Loggoljuk a hibát
        }
        return "redirect:/tasks"; // Archiválás után visszatérünk a főlistára
    }

    // ÚJ VÉGPONT archivált feladat visszaállításához
    @PostMapping("/unarchive/{id}")
    public String unarchiveTask(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taskService.unarchiveTask(id);
            redirectAttributes.addFlashAttribute("success", "Task unarchived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error unarchiving task: " + e.getMessage());
            e.printStackTrace(); // Loggoljuk a hibát
        }
        return "redirect:/tasks/archive"; // Visszaállítás után visszatérünk az archív listára
    }


    @GetMapping("/due-warning")
    public String getTasksWithDueDateWarnings(Model model) {
        List<Task> tasksWithDueWarnings = taskService.getTasksWithDueDateWarnings();
        model.addAttribute("tasksWithDueWarnings", tasksWithDueWarnings);
        return "tasks/due-warning";
    }

    // MÓDOSÍTOTT VÉGPONT: POST kérést fogad űrlapról, visszairányít
    // Az @ResponseBody és ResponseEntity<?> eltávolítva, mivel nem AJAX válasz lesz
    @PostMapping("/toggle-completed/{id}")
    public String toggleCompleted(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taskService.toggleTaskCompleted(id);
            // Siker esetén is visszairányítunk, nincs külön success üzenet itt
            // redirectAttributes.addFlashAttribute("success", "Task status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error toggling task completed status: " + e.getMessage());
            e.printStackTrace(); // Loggoljuk a hibát a szerver oldalon
        }
        // Visszairányítunk a fő feladatlistára, hogy frissüljön az oldal
        return "redirect:/tasks";
    }
}
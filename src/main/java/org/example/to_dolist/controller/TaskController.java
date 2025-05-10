package org.example.to_dolist.controller;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskStatus;
import org.example.to_dolist.domain.Task.TaskPriority;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String listTasks(Model model,
                            @RequestParam(defaultValue = "id") String sortBy,
                            @RequestParam(
                                    defaultValue = "asc") String sortDirection,
                            @RequestParam(
                                    value =
                                            "userId",
                                    required = false) String userId) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(sortDirection), sortBy);
        model.addAttribute(
                "tasks", taskService.getAllTasks(userId, sort));
        model.addAttribute(
                "currentSortBy", sortBy);
        model.addAttribute(
                "currentSortDirection", sortDirection);
        model.addAttribute(
                "users", userService.getAllUsers());
        model.addAttribute(
                "currentUserId", userId);
        model.addAttribute(
                "isArchiveView", false);
        return "tasks/tasks";
    }

    @GetMapping("/archive")
    public String listArchivedTasks(Model model,
                                    @RequestParam(
                                            defaultValue = "id")
                                    String sortBy,
                                    @RequestParam(
                                            defaultValue =
                                                    "asc") String sortDirection,
                                    @RequestParam(
                                            value = "userId",
                                            required = false)
                                        String userId) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        model.addAttribute(
                "tasks", taskService.getAllArchivedTasks(userId, sort));
        model.addAttribute(
                "currentSortBy", sortBy);
        model.addAttribute(
                "currentSortDirection", sortDirection);
        model.addAttribute(
                "users", userService.getAllUsers());
        model.addAttribute(
                "currentUserId", userId);
        model.addAttribute(
                "isArchiveView", true);
        return "tasks/archive";
    }

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "tasks/view-task";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute(
                "task", new Task());
        model.addAttribute(
                "statuses", TaskStatus.values());
        model.addAttribute(
                "priorities", TaskPriority.values());
        model.addAttribute(
                "users", userService.getAllUsers());
        return "tasks/create-task";
    }

    @PostMapping
    public String saveTask(@ModelAttribute(
            "task") Task task,
                           BindingResult bindingResult,
                           @RequestParam(
                                   value = "userId",
                                   required = false) UUID userId,
                           @RequestParam(
                                   "priority") Task.TaskPriority priority,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "statuses", TaskStatus.values());
            model.addAttribute(
                    "priorities", TaskPriority.values());
            model.addAttribute(
                    "users", userService.getAllUsers());
            model.addAttribute(
                    "error", "Validation errors occurred.");
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
            taskService.createTask(task);
            redirectAttributes.addFlashAttribute(
                    "success", "Task created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Error creating task: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute(
                "task", task);
        model.addAttribute(
                "statuses", TaskStatus.values());
        model.addAttribute(
                "priorities", TaskPriority.values());
        model.addAttribute(
                "users", userService.getAllUsers());
        return "tasks/edit-task";
    }

    @PostMapping("/edit")
    public String updateTask(@ModelAttribute("task") Task task,
                             BindingResult bindingResult,
                             @RequestParam(
                                     value = "userId",
                                     required = false) UUID userId,
                             @RequestParam(
                                     "priority")
                                 Task.TaskPriority priority,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        UUID taskId = task.getId();
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "statuses", TaskStatus.values());
            model.addAttribute(
                    "priorities", TaskPriority.values());
            model.addAttribute(
                    "users", userService.getAllUsers());
            model.addAttribute(
                    "task", taskService.getTaskById(taskId));
            model.addAttribute(
                    "error", "Validation errors occurred.");
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
            redirectAttributes.addFlashAttribute(
                    "success", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Error updating task: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id,
                         RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Task deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Error deleting task: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks";
    }

    @PostMapping("/archive/{id}")
    public String archiveTask(@PathVariable UUID id,
                              RedirectAttributes redirectAttributes) {
        try {
            taskService.archiveTask(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Task archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Error archiving task: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks";
    }

    @PostMapping("/unarchive/{id}")
    public String unarchiveTask(@PathVariable UUID id,
                                RedirectAttributes redirectAttributes) {
        try {
            taskService.unarchiveTask(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Task unarchived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Error unarchiving task: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks/archive";
    }

    @GetMapping("/due-warning")
    public String getTasksWithDueDateWarnings(Model model) {
        List<Task> tasksWithDueWarnings =
                taskService.getTasksWithDueDateWarnings();
        model.addAttribute("tasksWithDueWarnings", tasksWithDueWarnings);
        return "tasks/due-warning";
    }

    @PostMapping("/toggle-completed/{id}")
    public String toggleCompleted(@PathVariable UUID id,
                                  RedirectAttributes redirectAttributes) {
        try {
            taskService.toggleTaskCompleted(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Error toggling task "
                            +
                            "completed status: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tasks";
    }
}
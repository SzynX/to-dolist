package org.example.to_dolist.controller;

import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskPriority;
import org.example.to_dolist.service.TaskService;
import org.example.to_dolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskService taskService;
    private UserService userService;
    private TaskController taskController;
    private Model model;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        userService = mock(UserService.class);
        model = mock(Model.class);
        taskController = new TaskController(taskService, userService);
    }

    @Test
    void listTasks_returnsTasksView() {
        when(taskService.getAllTasks(null, null)).thenReturn(Collections.emptyList());
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = taskController.listTasks(model, "id", "asc", null);

        assertEquals("tasks/tasks", viewName);
        verify(model).addAttribute(eq("tasks"), anyList());
        verify(model).addAttribute(eq("users"), anyList());
    }

    @Test
    void listArchivedTasks_returnsArchiveView() {
        when(taskService.getAllArchivedTasks(null, null)).thenReturn(Collections.emptyList());
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = taskController.listArchivedTasks(model, "id", "asc", null);

        assertEquals("tasks/archive", viewName);
        verify(model).addAttribute(eq("tasks"), anyList());
        verify(model).addAttribute(eq("users"), anyList());
    }

    @Test
    void viewTask_returnsViewTaskPage() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        when(taskService.getTaskById(id)).thenReturn(task);

        String view = taskController.viewTask(id, model);

        assertEquals("tasks/view-task", view);
        verify(model).addAttribute("task", task);
    }

    @Test
    void createForm_returnsCreateFormView() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String view = taskController.createForm(model);

        assertEquals("tasks/create-task", view);
        verify(model).addAttribute(eq("task"), any(Task.class));
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("priorities"), any());
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void saveTask_successfulRedirect() {
        Task task = new Task();
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = taskController.saveTask(task, bindingResult, null, TaskPriority.HIGH, model, redirectAttributes);

        verify(taskService).createTask(task);
        assertEquals("redirect:/tasks", view);
    }

    @Test
    void saveTask_withValidationErrors_returnsCreateView() {
        Task task = new Task();
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        String view = taskController.saveTask(task, bindingResult, null, TaskPriority.MEDIUM, model, redirectAttributes);

        assertEquals("tasks/create-task", view);
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("priorities"), any());
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void updateTask_successfulUpdateRedirect() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = taskController.updateTask(task, bindingResult, null, TaskPriority.LOW, model, redirectAttributes);

        verify(taskService).updateTask(task);
        assertEquals("redirect:/tasks", view);
    }

    @Test
    void updateTask_withValidationErrors_returnsEditView() {
        Task task = new Task();
        task.setId(UUID.randomUUID());
        BindingResult bindingResult = mock(BindingResult.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        String view = taskController.updateTask(task, bindingResult, null, TaskPriority.HIGH, model, redirectAttributes);

        assertEquals("tasks/edit-task", view);
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("priorities"), any());
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void editForm_returnsEditTaskView() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        when(taskService.getTaskById(id)).thenReturn(task);
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String view = taskController.editForm(id, model);

        assertEquals("tasks/edit-task", view);
        verify(model).addAttribute("task", task);
        verify(model).addAttribute(eq("statuses"), any());
        verify(model).addAttribute(eq("priorities"), any());
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void deleteTask_successfulDelete() {
        UUID id = UUID.randomUUID();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = taskController.delete(id, redirectAttributes);

        verify(taskService).deleteTask(id);
        assertEquals("redirect:/tasks", view);
    }

    @Test
    void archiveTask_successful() {
        UUID id = UUID.randomUUID();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = taskController.archiveTask(id, redirectAttributes);

        verify(taskService).archiveTask(id);
        assertEquals("redirect:/tasks", view);
    }

    @Test
    void unarchiveTask_successful() {
        UUID id = UUID.randomUUID();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = taskController.unarchiveTask(id, redirectAttributes);

        verify(taskService).unarchiveTask(id);
        assertEquals("redirect:/tasks/archive", view);
    }

    @Test
    void toggleCompleted_successful() {
        UUID id = UUID.randomUUID();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = taskController.toggleCompleted(id, redirectAttributes);

        verify(taskService).toggleTaskCompleted(id);
        assertEquals("redirect:/tasks", view);
    }

    @Test
    void getTasksWithDueDateWarnings_returnsView() {
        when(taskService.getTasksWithDueDateWarnings()).thenReturn(List.of());

        String view = taskController.getTasksWithDueDateWarnings(model);

        assertEquals("tasks/due-warning", view);
        verify(model).addAttribute("tasksWithDueWarnings", List.of());
    }
}

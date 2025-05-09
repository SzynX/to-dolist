package org.example.to_dolist.controller;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.service.TodoService;
import org.example.to_dolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TodoControllerTest {

    private TodoService todoService;
    private UserService userService;
    private TodoController todoController;
    private Model model;

    @BeforeEach
    void setUp() {
        todoService = mock(TodoService.class);
        userService = mock(UserService.class);
        model = mock(Model.class);
        todoController = new TodoController(todoService, userService);
    }

    @Test
    void getAllTodos_shouldAddTodosToModelAndReturnView() {
        when(todoService.getAllTodos()).thenReturn(Collections.emptyList());

        String viewName = todoController.getAllTodos(model);

        verify(model).addAttribute("todos", Collections.emptyList());
        assertEquals("todos/todos", viewName);
    }

    @Test
    void createTodoForm_shouldReturnCreateFormViewWithEmptyTodoAndUsers() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = todoController.createTodoForm(model);

        verify(model).addAttribute(eq("todo"), any(Todo.class));
        verify(model).addAttribute("users", Collections.emptyList());
        assertEquals("todos/create-todo", viewName);
    }

    @Test
    void saveTodo_shouldCallServiceAndRedirect() {
        Todo todo = new Todo();

        String result = todoController.saveTodo(todo);

        verify(todoService).save(todo);
        assertEquals("redirect:/todos/list", result);
    }

    @Test
    void editTodoForm_shouldAddTodoAndUsersToModelAndReturnEditView() {
        UUID todoId = UUID.randomUUID();
        Todo todo = new Todo();
        when(todoService.findById(todoId)).thenReturn(todo);
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(new User()));

        String viewName = todoController.editTodoForm(todoId, model);

        verify(model).addAttribute("todo", todo);
        verify(model).addAttribute(eq("users"), anyList());
        assertEquals("todos/edit-todo", viewName);
    }

    @Test
    void updateTodo_shouldCallEditAndRedirect() {
        Todo todo = new Todo();

        String result = todoController.updateTodo(todo);

        verify(todoService).edit(todo);
        assertEquals("redirect:/todos/list", result);
    }

    @Test
    void deleteTodo_shouldCallDeleteAndRedirect() {
        UUID id = UUID.randomUUID();

        String result = todoController.deleteTodo(id);

        verify(todoService).deleteById(id);
        assertEquals("redirect:/todos/list", result);
    }

    @Test
    void getAllTasks_shouldAddTasksToModelAndReturnTasksView() {
        when(todoService.getAllTodos()).thenReturn(Collections.emptyList());

        String viewName = todoController.getAllTasks(model);

        verify(model).addAttribute("tasks", Collections.emptyList());
        assertEquals("tasks/tasks", viewName);
    }
}

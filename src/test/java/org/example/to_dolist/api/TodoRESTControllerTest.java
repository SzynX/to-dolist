package org.example.to_dolist.api;

import org.example.to_dolist.controller.api.TodoRESTController;
import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TodoRESTControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoRESTController todoRESTController;

    private final UUID testTodoId = UUID.randomUUID();
    private final Todo testTodo = Todo.builder()
            .id(testTodoId)
            .title("Test Todo")
            .description("Test Description")
            .completed(false)
            .dueDate(LocalDate.now().plusDays(7))
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(todoRESTController).build();
    }

    @Test
    void getAllTodos_ShouldReturnTodoList() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(testTodo));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Todo"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void getTodoById_ShouldReturnTodoWhenExists() throws Exception {
        when(todoService.findById(testTodoId)).thenReturn(testTodo);

        mockMvc.perform(get("/api/todos/{id}", testTodoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodoId.toString()));
    }

    @Test
    void getTodoById_ShouldReturnNotFoundWhenMissing() throws Exception {
        when(todoService.findById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(get("/api/todos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTodo_ShouldReturnCreatedTodo() throws Exception {
        when(todoService.save(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "title": "New Todo",
                            "description": "New Description",
                            "dueDate": "%s"
                        }""".formatted(LocalDate.now().plusDays(5))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void updateTodo_ShouldUpdateExistingTodo() throws Exception {
        when(todoService.findById(testTodoId)).thenReturn(testTodo);
        when(todoService.edit(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(put("/api/todos/{id}", testTodoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "title": "Updated Title",
                            "completed": true
                        }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void updateTodo_ShouldReturnNotFoundForInvalidId() throws Exception {
        when(todoService.findById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(put("/api/todos/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTodo_ShouldReturnNoContentWhenDeleted() throws Exception {
        when(todoService.findById(testTodoId)).thenReturn(testTodo);

        mockMvc.perform(delete("/api/todos/{id}", testTodoId))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteById(testTodoId);
    }

    @Test
    void deleteTodo_ShouldReturnNotFoundWhenMissing() throws Exception {
        when(todoService.findById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(delete("/api/todos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(todoService, never()).deleteById(any());
    }
}
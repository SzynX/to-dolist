package org.example.to_dolist.service;

import org.example.to_dolist.domain.Todo;
import org.example.to_dolist.exception.NoSuchEntityException;
import org.example.to_dolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    private UUID testTodoId;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodoId = UUID.randomUUID();
        testTodo = Todo.builder()
                .id(testTodoId)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }



    @Test
    void getAllTodos_ShouldReturnAllTodos() {
        Todo anotherTodo = Todo.builder()
                .id(UUID.randomUUID())
                .title("Another Todo")
                .description("Another Description")
                .completed(true)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        when(todoRepository.findAll()).thenReturn(List.of(testTodo, anotherTodo));

        List<Todo> result = todoService.getAllTodos();

        assertThat(result).hasSize(2).contains(testTodo, anotherTodo);
        verify(todoRepository, times(1)).findAll();
    }



    @Test
    void save_ShouldSaveAndReturnTodo() {
        when(todoRepository.save(testTodo)).thenReturn(testTodo);

        Todo result = todoService.save(testTodo);

        assertThat(result).isEqualTo(testTodo);
        verify(todoRepository, times(1)).save(testTodo);
    }



    @Test
    void edit_ShouldUpdateAndReturnTodo() {
        Todo updatedTodo = Todo.builder()
                .id(testTodoId)
                .title("Updated Title")
                .description("Updated Description")
                .completed(true)
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        when(todoRepository.save(updatedTodo)).thenReturn(updatedTodo);

        Todo result = todoService.edit(updatedTodo);

        assertThat(result).isEqualTo(updatedTodo);
        verify(todoRepository, times(1)).save(updatedTodo);
    }



    @Test
    void findById_ShouldReturnTodo_WhenExists() {
        when(todoRepository.findById(testTodoId)).thenReturn(Optional.of(testTodo));

        Todo result = todoService.findById(testTodoId);

        assertThat(result).isEqualTo(testTodo);
        verify(todoRepository, times(1)).findById(testTodoId);
    }

    @Test
    void findById_ShouldThrowNoSuchEntityException_WhenNotExists() {
        when(todoRepository.findById(testTodoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(testTodoId))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("There was no todo found for this id:");

        verify(todoRepository, times(1)).findById(testTodoId);
    }



    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        todoService.deleteById(testTodoId);

        verify(todoRepository, times(1)).deleteById(testTodoId);
    }
}
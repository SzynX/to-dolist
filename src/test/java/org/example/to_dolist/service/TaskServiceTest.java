package com.example.todolist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.todolist.domain.Task;
import com.example.todolist.exception.NoSuchEntityException;
import com.example.todolist.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepositoryMock;

    @InjectMocks
    private TaskService underTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTasksHappyPath() {
        // GIVEN
        List<Task> expectedTasks = List.of(
                Task.builder()
                        .id(UUID.randomUUID())
                        .title("Task 1")
                        .dueDate(LocalDate.now())
                        .build(),
                Task.builder()
                        .id(UUID.randomUUID())
                        .title("Task 2")
                        .dueDate(LocalDate.now().plusDays(1))
                        .build()
        );

        when(taskRepositoryMock.findAll()).thenReturn(expectedTasks);

        // WHEN
        List<Task> result = underTest.getAllTasks();

        // THEN
        assertEquals(expectedTasks, result);
    }

    @Test
    void findByIdWhenTaskFound() {
        UUID id = UUID.randomUUID();
        Task expectedTask = Task.builder()
                .id(id)
                .title("Important Task")
                .dueDate(LocalDate.now())
                .build();

        when(taskRepositoryMock.findById(id)).thenReturn(Optional.of(expectedTask));

        Task result = underTest.findById(id);

        assertEquals(expectedTask, result);
    }

    @Test
    void findByIdWhenTaskIsMissing() {
        UUID id = UUID.randomUUID();
        when(taskRepositoryMock.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchEntityException.class, () -> underTest.findById(id));

        assertEquals("There was no task with id: " + id, exception.getMessage());
    }

    @Test
    void deleteByIDHappyPath() {
        UUID id = UUID.randomUUID();
        underTest.deleteById(id);
        verify(taskRepositoryMock).deleteById(id);
    }

    @Test
    void saveHappyPath() {
        Task expectedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("New Task")
                .dueDate(LocalDate.now())
                .build();

        when(taskRepositoryMock.save(expectedTask)).thenReturn(expectedTask);

        Task result = underTest.save(expectedTask);

        assertEquals(expectedTask, result);
    }

    @Test
    void editHappyPath() {
        Task updatedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Updated Task")
                .dueDate(LocalDate.now())
                .build();

        when(taskRepositoryMock.save(updatedTask)).thenReturn(updatedTask);

        Task result = underTest.save(updatedTask);

        assertEquals(updatedTask, result);
    }
}

package org.example.to_dolist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.example.to_dolist.domain.Task;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepositoryMock;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_whenFound_returnsTask() {
        UUID taskId = UUID.randomUUID();
        Task expectedTask = Task.builder()
                .id(taskId)
                .title("Test Task")
                .dueDate(LocalDate.now())
                .build();

        when(taskRepositoryMock.findById(taskId)).thenReturn(Optional.of(expectedTask));

        Task result = taskService.getTaskById(taskId);

        assertEquals(expectedTask, result);
    }

    @Test
    void getTaskById_whenNotFound_throwsException() {
        UUID taskId = UUID.randomUUID();

        when(taskRepositoryMock.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));
    }

    @Test
    void createTask_savesAndReturnsTask() {
        Task newTask = Task.builder()
                .title("New Task")
                .dueDate(LocalDate.now())
                .build();

        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("New Task")
                .dueDate(newTask.getDueDate())
                .build();

        when(taskRepositoryMock.save(newTask)).thenReturn(savedTask);

        Task result = taskService.createTask(newTask);

        assertEquals(savedTask, result);
    }
}
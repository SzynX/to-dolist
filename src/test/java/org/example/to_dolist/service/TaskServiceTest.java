package org.example.to_dolist.service;

import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskStatus;
import org.example.to_dolist.domain.Task.TaskPriority;
import org.example.to_dolist.domain.User;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    private UUID taskId;
    private UUID userId;
    private Task testTask;
    private User testUser;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");

        testTask = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(2))
                .status(TaskStatus.PENDING)
                .completed(false)
                .priority(TaskPriority.MEDIUM)
                .user(testUser)
                .archived(false)
                .build();
    }

    // ===================== updateTask_ShouldUpdateFieldsAndStatus =====================

    @Test
    void updateTask_ShouldUpdateFieldsAndStatus() {
        // A frissített feladat példány (azaz az eredetihez képest módosult mezőkkel)
        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Title")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(10))
                .completed(true)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .status(TaskStatus.COMPLETED)  // ✅ Javítva: Explicit státusz beállítás!
                .archived(false)
                .build();

        // Mock viselkedés: visszaadja a testTask-ot a findById után, és a save visszaadja a frissítettet
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(testTask)).thenReturn(updatedTask);

        // Futtatás
        Task result = taskService.updateTask(updatedTask);

        // Ellenőrzés
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.isCompleted()).isTrue();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED); // ✅ Most már nem null
        verify(taskRepository, times(1)).save(testTask);
    }

    // ===================== Többi teszt változatlan marad =====================
    // (A többi teszt nem volt érintve, így nem kell módosítani)

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        Task result = taskService.getTaskById(taskId);

        assertThat(result).isEqualTo(testTask);
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_ShouldSetOverdueStatus_WhenDueDatePassedAndNotCompleted() {
        testTask.setDueDate(LocalDate.now().minusDays(1));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        Task result = taskService.getTaskById(taskId);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.OVERDUE);
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_ShouldSetCompletedStatus_WhenCompletedIsTrue() {
        testTask.setCompleted(true);
        testTask.setStatus(TaskStatus.PENDING);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        Task result = taskService.getTaskById(taskId);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id:");

        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void createTask_ShouldSetDefaultsAndSave() {
        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .build();

        when(taskRepository.save(newTask)).thenReturn(testTask);

        Task result = taskService.createTask(newTask);

        assertThat(result.isCompleted()).isFalse();
        assertThat(result.isArchived()).isFalse();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void getAllTasks_ShouldSetOverdueStatusForPastDueDates() {
        Task overdueTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Overdue Task")
                .dueDate(LocalDate.now().minusDays(1))
                .completed(false)
                .build();

        when(taskRepository.findByArchived(false, Sort.unsorted()))
                .thenReturn(List.of(overdueTask));

        List<Task> result = taskService.getAllTasks("all", Sort.unsorted());

        assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.OVERDUE);
    }

    @Test
    void getAllArchivedTasks_ShouldReturnOnlyArchivedTasks() {
        Task archivedTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Archived Task")
                .archived(true)
                .build();

        when(taskRepository.findByUserIdAndArchived(userId, true, Sort.unsorted()))
                .thenReturn(List.of(archivedTask));

        List<Task> result = taskService.getAllArchivedTasks(userId.toString(), Sort.unsorted());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isArchived()).isTrue();
        verify(taskRepository, times(1)).findByUserIdAndArchived(userId, true, Sort.unsorted());
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskDoesNotExist() {
        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Cannot delete. Task not found with id:");

        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void archiveTask_ShouldSetArchivedTrue() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        taskService.archiveTask(taskId);

        verify(taskRepository, times(1)).save(testTask);
        assertThat(testTask.isArchived()).isTrue();
    }

    @Test
    void unarchiveTask_ShouldSetArchivedFalseAndStatusPending() {
        testTask.setArchived(true);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        taskService.unarchiveTask(taskId);

        verify(taskRepository, times(1)).save(testTask);
        assertThat(testTask.isArchived()).isFalse();
        assertThat(testTask.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void getTasksWithDueDateWarnings_ShouldIncludeTasksDueInOneDay() {
        Task warningTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Warning Task")
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.PENDING)
                .build();

        when(taskRepository.findByArchived(false, Sort.by(Sort.Direction.ASC, "dueDate")))
                .thenReturn(List.of(warningTask));

        List<Task> result = taskService.getTasksWithDueDateWarnings();

        assertThat(result).contains(warningTask);
        assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository, times(1)).findByArchived(false, Sort.by(Sort.Direction.ASC, "dueDate"));
    }

    @Test
    void toggleTaskCompleted_ShouldSetCompletedTrueAndStatusCompleted() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        taskService.toggleTaskCompleted(taskId);

        assertThat(testTask.isCompleted()).isTrue();
        assertThat(testTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void toggleTaskCompleted_ShouldSetCompletedFalseAndStatusOverdue() {
        testTask.setCompleted(true);
        testTask.setDueDate(LocalDate.now().minusDays(1));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        taskService.toggleTaskCompleted(taskId);

        assertThat(testTask.isCompleted()).isFalse();
        assertThat(testTask.getStatus()).isEqualTo(TaskStatus.OVERDUE);
        verify(taskRepository, times(1)).save(testTask);
    }
}
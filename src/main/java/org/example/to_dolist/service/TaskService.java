package org.example.to_dolist.service;


import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskStatus;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + id
                ));

        updateTaskStatusBasedOnCompletionAndDueDate(task);

        return task;
    }

    public Task createTask(Task task) {
        task.setCompleted(false);
        task.setArchived(false);
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(String userId, Sort sort) {
        List<Task> tasks;

        if (userId == null
                || userId.isEmpty()
                || "all".equalsIgnoreCase(userId)) {
            tasks = taskRepository.findByArchived(false, sort);
        } else if ("unassigned".equalsIgnoreCase(userId)) {
            tasks = taskRepository.findByUserIsNullAndArchived(
                    false,
                    sort
            );
        } else {
            try {
                UUID userUuid = UUID.fromString(userId);
                tasks = taskRepository.findByUserIdAndArchived(
                        userUuid,
                        false,
                        sort
                );
            } catch (IllegalArgumentException e) {
                tasks = taskRepository.findByArchived(false, sort);
            }
        }

        LocalDate currentDate = LocalDate.now();
        tasks.forEach(
                this::updateTaskStatusBasedOnCompletionAndDueDate
        );

        return tasks;
    }

    public List<Task> getAllArchivedTasks(String userId, Sort sort) {
        if (userId == null
                || userId.isEmpty()
                || "all".equalsIgnoreCase(userId)) {
            return taskRepository.findByArchived(true, sort);
        } else if ("unassigned".equalsIgnoreCase(userId)) {
            return taskRepository.findByUserIsNullAndArchived(
                    true,
                    sort
            );
        } else {
            try {
                UUID userUuid = UUID.fromString(userId);
                return taskRepository.findByUserIdAndArchived(
                        userUuid,
                        true,
                        sort
                );
            } catch (IllegalArgumentException e) {
                return taskRepository.findByArchived(true, sort);
            }
        }
    }

    /**
     * Feladat módosítása.
     */
    public Task updateTask(Task updatedTask) {
        Task existingTask = getTaskById(updatedTask.getId());

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setUser(updatedTask.getUser());

        if (existingTask.isCompleted() != updatedTask.isCompleted()) {
            if (updatedTask.isCompleted()) {
                existingTask.setStatus(TaskStatus.COMPLETED);
            } else {
                if (!existingTask.isArchived()) {
                    if (updatedTask.getDueDate() != null
                            && updatedTask.getDueDate()
                            .isBefore(LocalDate.now())) {
                        existingTask.setStatus(TaskStatus.OVERDUE);
                    } else {
                        existingTask.setStatus(TaskStatus.PENDING);
                    }
                }
            }
        } else {
            existingTask.setStatus(updatedTask.getStatus());
        }

        existingTask.setCompleted(updatedTask.isCompleted());

        return taskRepository.save(existingTask);
    }

    /**
     * Feladat törlése.
     */
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(
                    "Cannot delete. Task not found "
                            + "with id: " + id
            );
        }
        taskRepository.deleteById(id);
    }

    /**
     * Feladat archiválása.
     */
    @Transactional
    public void archiveTask(UUID id) {
        Task task = getTaskById(id);
        task.setArchived(true);
        taskRepository.save(task);
    }

    /**
     * Archivált feladat visszaállítása. (unarchive).
     */
    @Transactional
    public void unarchiveTask(UUID id) {
        Task task = getTaskById(id);
        task.setArchived(false);

        if (!task.isCompleted() && task.getStatus() != TaskStatus.OVERDUE) {
            task.setStatus(TaskStatus.PENDING);
        }

        taskRepository.save(task);
    }

    public List<Task> getTasksWithDueDateWarnings() {
        LocalDate currentDate = LocalDate.now();

        return taskRepository.findByArchived(
                        false,
                        Sort.by(Sort.Direction.ASC, "dueDate")
                ).stream()
                .filter(task -> !task.isCompleted()
                                && task.getDueDate() != null
                                && !task.getDueDate().isAfter(
                                currentDate.plusDays(1)
                        )
                )
                .peek(this::updateTaskStatusBasedOnDueDate)
                .collect(Collectors.toList());
    }

    /**
     * Feladat kész állapotának váltása.
     */
    @Transactional
    public void toggleTaskCompleted(UUID id) {
        Task task = getTaskById(id);

        if (!task.isArchived()) {
            boolean newCompletedState = !task.isCompleted();
            task.setCompleted(newCompletedState);

            if (newCompletedState) {
                task.setStatus(TaskStatus.COMPLETED);
            } else {
                updateTaskStatusBasedOnDueDate(task);
            }

            taskRepository.save(task);
        }
    }

    private void updateTaskStatusBasedOnCompletionAndDueDate(Task task) {
        if (!task.isCompleted()) {
            updateTaskStatusBasedOnDueDate(task);
        } else if (!task.getStatus().equals(TaskStatus.COMPLETED)) {
            task.setStatus(TaskStatus.COMPLETED);
        }
    }

    private void updateTaskStatusBasedOnDueDate(Task task) {
        if (task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDate.now())) {
            task.setStatus(TaskStatus.OVERDUE);
        } else {
            task.setStatus(TaskStatus.PENDING);
        }
    }
}
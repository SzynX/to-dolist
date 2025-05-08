package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Cannot delete. Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Javított szűrő a lejárt és közelgő határidőkhöz
    public List<Task> getTasksWithDueDateWarnings() {
        LocalDate currentDate = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(task -> {
                    if (task.getDueDate() != null) {
                        long daysUntilDue = task.getDueDate().toEpochDay() - currentDate.toEpochDay();
                        return daysUntilDue <= 1; // Lejárt és 1 napon belüli határidők
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
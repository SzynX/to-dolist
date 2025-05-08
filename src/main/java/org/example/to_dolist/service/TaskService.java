package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importálni

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
        // Új feladat létrehozásakor alapértelmezetten legyen nem completed
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Task task) {
        // Biztosítsuk, hogy a completed státusz is frissüljön
        Task existingTask = getTaskById(task.getId());
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        existingTask.setUser(task.getUser());
        existingTask.setCompleted(task.isCompleted()); // Frissítjük a completed mezőt is
        return taskRepository.save(existingTask);
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
                    // Csak azokat a feladatokat listázzuk, amik nincsenek készen!
                    if (!task.isCompleted() && task.getDueDate() != null) {
                        long daysUntilDue = task.getDueDate().toEpochDay() - currentDate.toEpochDay();
                        return daysUntilDue <= 1; // Lejárt (<=0) és 1 napon belüli (==1) határidők
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // ÚJ metódus a completed státusz váltásához
    @Transactional // Tranzakciókezelés hozzáadása
    public void toggleTaskCompleted(UUID id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.isCompleted()); // Státusz váltása
        taskRepository.save(task); // Mentés
    }
}
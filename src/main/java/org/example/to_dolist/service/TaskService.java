package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort; // Importálni

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
        task.setCompleted(false); // Új feladat alapértelmezetten nem completed
        return taskRepository.save(task);
    }

    // Módosított metódus: elfogad Sort paramétert
    public List<Task> getAllTasks(Sort sort) {
        return taskRepository.findAll(sort); // Lekérdezés rendezéssel
    }

    // Megtartjuk az eredeti getAllTasks() metódust az egyszerűség kedvéért, alapértelmezett rendezéssel
    public List<Task> getAllTasks() {
        // Alapértelmezett rendezés ID szerint növekvőben, ha nincs megadva más
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }


    public Task updateTask(Task task) {
        Task existingTask = getTaskById(task.getId());
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        existingTask.setUser(task.getUser());
        existingTask.setCompleted(task.isCompleted());
        return taskRepository.save(existingTask);
    }

    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Cannot delete. Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Javított szűrő a lejárt és közelgő határidőkhöz (itt nem használunk rendezést)
    public List<Task> getTasksWithDueDateWarnings() {
        LocalDate currentDate = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(task -> {
                    if (!task.isCompleted() && task.getDueDate() != null) {
                        long daysUntilDue = task.getDueDate().toEpochDay() - currentDate.toEpochDay();
                        return daysUntilDue <= 1;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleTaskCompleted(UUID id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }
}
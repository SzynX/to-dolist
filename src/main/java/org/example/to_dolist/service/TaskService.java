package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.User; // Importálni
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService; // Hozzáadjuk a UserService dependency-t

    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    public Task createTask(Task task) {
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    // Módosított metódus: elfogad opcionális userId paramétert és Sort paramétert
    public List<Task> getAllTasks(String userId, Sort sort) {
        if (userId == null || userId.isEmpty() || "all".equalsIgnoreCase(userId)) {
            // Ha nincs szűrés (null, üres string vagy "all"), kérjük le az összes feladatot rendezéssel
            return taskRepository.findAll(sort);
        } else if ("unassigned".equalsIgnoreCase(userId)) {
            // Ha "unassigned" a szűrés, kérjük le a felhasználó nélküli feladatokat rendezéssel
            return taskRepository.findByUserIsNull(sort);
        } else {
            // Ha konkrét felhasználó ID van megadva, kérjük le az ő feladatait rendezéssel
            try {
                UUID userUuid = UUID.fromString(userId);
                // Ellenőrizzük, hogy a felhasználó létezik-e, mielőtt szűrünk (opcionális, de jó gyakorlat)
                // User user = userService.getUserById(userUuid); // Lehet szükség rá, ha finomabb kezelés kell
                return taskRepository.findByUserId(userUuid, sort);
            } catch (IllegalArgumentException e) {
                // Hibás UUID formátum esetén visszatérhetünk üres listával vagy dobhatunk kivételt
                // Jelenleg visszatérünk az összes feladattal rendezve, mintha nem lenne szűrés
                return taskRepository.findAll(sort); // Vagy üres lista: return List.of();
            }
        }
    }

    // Megtartjuk az alapértelmezett getAllTasks() metódust, bár most már a fenti metódus is tud alapértelmezettet kezelni
    public List<Task> getAllTasks() {
        return getAllTasks("all", Sort.by(Sort.Direction.ASC, "id")); // Hívjuk a módosított metódust alapértelmezett értékekkel
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

    // Javított szűrő a lejárt és közelgő határidőkhöz (itt nem használunk felhasználó szűrést vagy rendezést)
    public List<Task> getTasksWithDueDateWarnings() {
        LocalDate currentDate = LocalDate.now();

        return taskRepository.findAll().stream() // Esetleg finomíthatnánk ezt is, ha sok feladat van
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
package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.Task.TaskStatus;
import org.example.to_dolist.domain.Task.TaskPriority;
import org.example.to_dolist.domain.User;
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
    private final UserService userService;

    public Task getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        // Ellenőrizzük a státuszt lekéréskor is, ha nincs kész és lejárt (megjelenítéshez)
        // Ha nem archivált! Archivált feladatoknál nem fontos az overdue státusz a főlistán
        if (!task.isCompleted() && !task.isArchived() && task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            task.setStatus(TaskStatus.OVERDUE);
        }
        // Biztosítjuk, hogy a completed és státusz konzisztens legyen (lekéréskor is), ha nem archivált
        if (!task.isArchived()) {
            if(task.isCompleted() && task.getStatus() != TaskStatus.COMPLETED) {
                task.setStatus(TaskStatus.COMPLETED);
            } else if (!task.isCompleted() && task.getStatus() == TaskStatus.COMPLETED) {
                if (task.getStatus() != TaskStatus.OVERDUE) {
                    task.setStatus(TaskStatus.PENDING);
                }
            }
        }

        return task;
    }

    public Task createTask(Task task) {
        task.setCompleted(false);
        task.setArchived(false); // Új feladat alapértelmezetten nem archivált
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        return taskRepository.save(task);
    }

    // Módosított metódus: lekéri a NEM archivált feladatokat, figyelembe véve a felhasználó szűrést és rendezést
    public List<Task> getAllTasks(String userId, Sort sort) {
        List<Task> tasks;
        if (userId == null || userId.isEmpty() || "all".equalsIgnoreCase(userId)) {
            // Minden NEM archivált feladat
            tasks = taskRepository.findByArchived(false, sort);
        } else if ("unassigned".equalsIgnoreCase(userId)) {
            // NEM archivált, NEM hozzárendelt feladatok
            tasks = taskRepository.findByUserIsNullAndArchived(false, sort);
        } else {
            // NEM archivált, konkrét felhasználóhoz rendelt feladatok
            try {
                UUID userUuid = UUID.fromString(userId);
                tasks = taskRepository.findByUserIdAndArchived(userUuid, false, sort);
            } catch (IllegalArgumentException e) {
                tasks = taskRepository.findByArchived(false, sort); // Visszatérünk az összes nem archiválttal hibás ID esetén
            }
        }

        // Végigmegyünk a feladatokon és frissítjük a státuszt, ha lejárt és nincs kész (csak a megjelenítéshez, ha nem archivált)
        LocalDate currentDate = LocalDate.now();
        for (Task task : tasks) {
            if (!task.isCompleted() && !task.isArchived() && task.getDueDate() != null && task.getDueDate().isBefore(currentDate)) {
                task.setStatus(TaskStatus.OVERDUE);
            }
            // Biztosítjuk a completed és státusz konzisztenciát a nem archiváltaknál
            if (!task.isArchived()) {
                if(task.isCompleted() && task.getStatus() != TaskStatus.COMPLETED) {
                    task.setStatus(TaskStatus.COMPLETED);
                } else if (!task.isCompleted() && task.getStatus() == TaskStatus.COMPLETED) {
                    if (task.getStatus() != TaskStatus.OVERDUE) {
                        task.setStatus(TaskStatus.PENDING);
                    }
                }
            }
        }

        return tasks;
    }

    // ÚJ metódus: lekéri az ARCHIVÁLT feladatokat, figyelembe véve a felhasználó szűrést és rendezést
    public List<Task> getAllArchivedTasks(String userId, Sort sort) {
        List<Task> tasks;
        if (userId == null || userId.isEmpty() || "all".equalsIgnoreCase(userId)) {
            // Minden ARCHIVÁLT feladat
            tasks = taskRepository.findByArchived(true, sort);
        } else if ("unassigned".equalsIgnoreCase(userId)) {
            // ARCHIVÁLT, NEM hozzárendelt feladatok
            tasks = taskRepository.findByUserIsNullAndArchived(true, sort);
        } else {
            // ARCHIVÁLT, konkrét felhasználóhoz rendelt feladatok
            try {
                UUID userUuid = UUID.fromString(userId);
                tasks = taskRepository.findByUserIdAndArchived(userUuid, true, sort);
            } catch (IllegalArgumentException e) {
                tasks = taskRepository.findByArchived(true, sort); // Visszatérünk az összes archiválttal hibás ID esetén
            }
        }
        // Az archív listán nem szükséges a lejárt státuszt külön ellenőrizni/állítani a megjelenítéshez,
        // mert azok a feladatok már nem aktívak.

        return tasks;
    }


    public Task updateTask(Task task) {
        Task existingTask = getTaskById(task.getId());
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());

        // UPDATE ESETÉN: Ha a completed mező változik, állítsuk a státuszt is
        if (existingTask.isCompleted() != task.isCompleted()) {
            if (task.isCompleted()) {
                task.setStatus(TaskStatus.COMPLETED);
            } else {
                // Ha completed false lesz, visszaállítjuk a státuszt a határidő alapján (ha nem archivált)
                if (!existingTask.isArchived()) { // Csak ha a task még nincs archiválva
                    if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                        task.setStatus(TaskStatus.OVERDUE);
                    } else {
                        task.setStatus(TaskStatus.PENDING);
                    }
                } // Ha archiválva van, a státusz maradhat, vagy beállíthatjuk pl. COMPLETED ha completed volt.
            }
        } else {
            // Ha a completed mező nem változott, használjuk az űrlapról érkező státuszt
            existingTask.setStatus(task.getStatus());
        }

        // Archivált státusz frissítése, ha az űrlapról jön (pl. rejtett mezővel, ha később engednénk ezt az edit formon)
        // existingTask.setArchived(task.isArchived()); // Ezt most az archiveTask/unarchiveTask metódusok kezelik

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

    // ÚJ metódus feladat archiválásához
    @Transactional
    public void archiveTask(UUID id) {
        Task task = getTaskById(id); // getTaskById már frissíti az overdue státuszt lekéréskor (ha nem archivált)
        task.setArchived(true);
        // Opcionálisan: archiváláskor a státuszt is átállíthatod (pl. COMPLETED-re, ha kész volt)
        // if (task.isCompleted()) { task.setStatus(TaskStatus.COMPLETED); }
        taskRepository.save(task);
    }

    // ÚJ metódus archivált feladat visszaállításához
    @Transactional
    public void unarchiveTask(UUID id) {
        Task task = getTaskById(id); // getTaskById itt is ellenőrzi az overdue státuszt a nem archiváltakra (most már igaz lesz archived=false)
        task.setArchived(false);
        // Visszaállításkor beállíthatod a státuszt PENDING-re, ha nem volt Completed
        if (!task.isCompleted() && task.getStatus() != TaskStatus.OVERDUE) { // Csak ha nincs kész és nem lejárt
            task.setStatus(TaskStatus.PENDING);
        }
        taskRepository.save(task);
    }


    public List<Task> getTasksWithDueDateWarnings() {
        LocalDate currentDate = LocalDate.now();

        // Itt is csak a NEM archivált feladatokat figyelmeztetjük
        List<Task> tasks = taskRepository.findByArchived(false, Sort.by(Sort.Direction.ASC, "dueDate")).stream() // Rendezzük határidő szerint
                .filter(task -> {
                    if (!task.isCompleted() && task.getDueDate() != null) {
                        long daysUntilDue = task.getDueDate().toEpochDay() - currentDate.toEpochDay();
                        return daysUntilDue <= 1; // Lejárt (<=0) és 1 napon belüli (==1) határidők
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Frissítjük a státuszt a lejártakra (csak a megjelenítéshez)
        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getDueDate().isBefore(currentDate) && !task.isCompleted()) {
                task.setStatus(TaskStatus.OVERDUE);
            }
        }

        return tasks;
    }

    @Transactional
    public void toggleTaskCompleted(UUID id) {
        Task task = getTaskById(id);
        // Csak akkor váltsuk a completed státuszt, ha a feladat nincs archiválva
        if (!task.isArchived()) {
            task.setCompleted(!task.isCompleted());

            // Átállítjuk a státuszt a completed állapot alapján
            if (task.isCompleted()) {
                task.setStatus(TaskStatus.COMPLETED);
            } else {
                // Ha completed false lesz, visszaállítjuk a státuszt a határidő alapján
                if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                    task.setStatus(TaskStatus.OVERDUE); // Ha lejárt
                } else {
                    task.setStatus(TaskStatus.PENDING); // Ha nincs lejárt
                }
            }
            taskRepository.save(task);
        }
        // Ha archiválva van, nem csinálunk semmit a completed toggle-re
    }
}
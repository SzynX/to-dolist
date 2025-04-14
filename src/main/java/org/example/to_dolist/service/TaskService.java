package org.example.to_dolist.service;

import lombok.RequiredArgsConstructor;
import org.example.to_dolist.domain.Task;
import org.example.to_dolist.exception.TaskNotFoundException;
import org.example.to_dolist.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
}

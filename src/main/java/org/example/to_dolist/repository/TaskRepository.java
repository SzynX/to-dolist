package org.example.to_dolist.repository;

import org.example.to_dolist.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
package org.example.to_dolist.repository;

import org.example.to_dolist.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // Alap CRUD műveletek automatikusan biztosítva vannak
}

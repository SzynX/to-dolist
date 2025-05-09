package org.example.to_dolist.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

class TodoTest {

    private Todo todo;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "John Doe", LocalDate.of(1990, 1, 1), null);
        todo = Todo.builder()
                .id(UUID.randomUUID())
                .title("Test Todo")
                .dueDate(LocalDate.of(2025, 5, 10))
                .completed(false)
                .user(user)
                .build();
    }

    @Test
    void testTodoCreation() {
        assertNotNull(todo);
        assertEquals("Test Todo", todo.getTitle());
        assertEquals(LocalDate.of(2025, 5, 10), todo.getDueDate());
        assertFalse(todo.isCompleted());
        assertEquals(user, todo.getUser());
    }

    @Test
    void testSetTitle() {
        todo.setTitle("Updated Todo");
        assertEquals("Updated Todo", todo.getTitle());
    }

    @Test
    void testSetDueDate() {
        LocalDate newDueDate = LocalDate.of(2025, 6, 1);
        todo.setDueDate(newDueDate);
        assertEquals(newDueDate, todo.getDueDate());
    }

    @Test
    void testSetCompleted() {
        todo.setCompleted(true);
        assertTrue(todo.isCompleted());
    }

    @Test
    void testBidirectionalRelationship() {
        // Ellenőrizzük, hogy a Task user mezője helyesen van beállítva
        User newUser = new User(UUID.randomUUID(), "Jane Doe", LocalDate.of(1992, 2, 2), null);
        todo.setUser(newUser);

        assertEquals(newUser, todo.getUser());
    }

    @Test
    void testTodoUserBidirectionalRelationship() {
        // A Todo objektumot hozzáadjuk a User-hez
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Jane Doe")
                .dateOfBirth(LocalDate.of(1992, 2, 2))
                .build();

        Todo newTodo = Todo.builder()
                .id(UUID.randomUUID())
                .title("New Todo")
                .dueDate(LocalDate.of(2025, 6, 1))
                .completed(false)
                .user(user)
                .build();

        assertEquals(user, newTodo.getUser());
    }
}

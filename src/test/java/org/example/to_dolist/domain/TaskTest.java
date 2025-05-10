package org.example.to_dolist.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        // Dinamikusan állítjuk be a dueDate-t, hogy mindig a mai nap utáni legyen
        LocalDate futureDueDate = LocalDate.now().plusDays(1);

        user = new User(UUID.randomUUID(), "John Doe", LocalDate.of(1990, 1, 1), null);
        task = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Task Description")
                .dueDate(futureDueDate)
                .status(Task.TaskStatus.PENDING)
                .completed(false)
                .priority(Task.TaskPriority.HIGH)
                .user(user)
                .archived(false)
                .build();
    }

    @Test
    void testTaskCreation() {
        assertNotNull(task);
        assertEquals("Test Task", task.getTitle());
        assertEquals("Task Description", task.getDescription());
        assertEquals(Task.TaskStatus.PENDING, task.getStatus());
        assertEquals(Task.TaskPriority.HIGH, task.getPriority());
        assertEquals(user, task.getUser());
        assertFalse(task.isCompleted());
        assertFalse(task.isArchived());
    }

    @Test
    void testGetDaysUntilDueDate() {
        long daysUntilDueDate = task.getDaysUntilDueDate();
        assertTrue(daysUntilDueDate > 0);
    }

    @Test
    void testGetDueDateWarning() {
        // Beállítjuk a dueDate-t, hogy 1 nap hátra legyen
        task.setDueDate(LocalDate.now().plusDays(1));

        // Ha nem fejeződött be és nincs archiválva, figyelmeztetésnek meg kell jelennie
        String warning = task.getDueDateWarning();
        assertEquals("⏰ 1 nap van hátra!", warning);
    }

    @Test
    void testSetCompleted() {
        task.setCompleted(true);
        assertTrue(task.isCompleted());
    }

    @Test
    void testSetArchived() {
        task.setArchived(true);
        assertTrue(task.isArchived());
    }

    @Test
    void testBidirectionalRelationship() {
        User newUser = new User(UUID.randomUUID(), "Jane Doe", LocalDate.of(1992, 2, 2), null);
        task.setUser(newUser);
        assertEquals(newUser, task.getUser());
    }

    @Test
    void testTaskStatusEnum() {
        task.setStatus(Task.TaskStatus.COMPLETED);
        assertEquals(Task.TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void testTaskPriorityEnum() {
        task.setPriority(Task.TaskPriority.MEDIUM);
        assertEquals(Task.TaskPriority.MEDIUM, task.getPriority());
    }

    @Test
    void testDueDateWarningCompletedTask() {
        task.setCompleted(true);
        String warning = task.getDueDateWarning();
        assertNull(warning);  // Ha a feladat kész, nincs figyelmeztetés
    }

    @Test
    void testDueDateWarningArchivedTask() {
        task.setArchived(true);
        String warning = task.getDueDateWarning();
        assertNull(warning);  // Ha a feladat archivált, nincs figyelmeztetés
    }
}
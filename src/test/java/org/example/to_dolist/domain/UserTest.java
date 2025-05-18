package org.example.to_dolist.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTest {

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        task = mock(Task.class);
    }

    @Test
    void testAddTask() {
        int initialSize = user.getTasks().size();

        user.addTask(task);

        assertEquals(initialSize + 1, user.getTasks().size(), "A task hozzáadása után nőnie kell a lista méretének.");
        verify(task).setUser(user);
    }

    @Test
    void testRemoveTask() {
        user.addTask(task); // Add the task first
        int initialSize = user.getTasks().size(); // Get the size after adding

        user.removeTask(task);

        assertEquals(initialSize - 1, user.getTasks().size(), "A task eltávolítása után csökkennie kell a lista méretének.");
        verify(task).setUser(null);
    }

    @Test
    void testAddTaskBidirectionalRelationship() {

        Task task = new Task();
        user.addTask(task);
        assertEquals(user, task.getUser(), "A task user-jának a User-nak kell lennie.");
    }


    @Test
    void testRemoveTaskBidirectionalRelationship() {
        user.addTask(task);
        user.removeTask(task);

        assertNull(task.getUser(), "A task user-jének null-nak kell lennie eltávolítás után.");
    }

    @Test
    void testUserAttributes() {
        UUID userId = UUID.randomUUID();
        String userName = "John Doe";
        LocalDate userDateOfBirth = LocalDate.of(1990, 1, 1);

        user.setId(userId);
        user.setName(userName);
        user.setDateOfBirth(userDateOfBirth);

        assertEquals(userId, user.getId(), "A User ID-nak meg kell egyeznie.");
        assertEquals(userName, user.getName(), "A User névnek meg kell egyeznie.");
        assertEquals(userDateOfBirth, user.getDateOfBirth(), "A User születési dátumának meg kell egyeznie.");
    }
}
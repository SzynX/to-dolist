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
        task = mock(Task.class); // Task osztály mockolása
    }

    @Test
    void testAddTask() {
        // Ellenőrizzük, hogy a task hozzáadásakor a lista növekszik
        int initialSize = user.getTasks().size();

        user.addTask(task);

        assertEquals(initialSize + 1, user.getTasks().size(), "A task hozzáadása után nőnie kell a lista méretének.");
        verify(task).setUser(user); // Ellenőrizzük, hogy a task user-t is beállítja
    }

    @Test
    void testRemoveTask() {
        // Hozzáadunk egy task-ot, majd eltávolítjuk
        user.addTask(task);
        int initialSize = user.getTasks().size();

        user.removeTask(task);

        assertEquals(initialSize - 1, user.getTasks().size(), "A task eltávolítása után csökkennie kell a lista méretének.");
        verify(task).setUser(null); // Ellenőrizzük, hogy a task user-t null-ra állítja
    }

    @Test
    void testAddTaskBidirectionalRelationship() {
        // Hozzunk létre egy valós Task objektumot
        Task task = new Task();

        // A feladatot hozzáadjuk a User-hez
        user.addTask(task);

        // Ellenőrizzük, hogy a Task user mezője a User objektumot tartalmazza
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
        // Teszteljük a User objektum attribútumait
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

package org.example.to_dolist.repository; // Ellenőrizd a csomagnevet!

import org.example.to_dolist.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Jelzi Spring számára, hogy ez egy repository bean
// Kiterjesztjük a JpaRepository-t: megadjuk az entitás típusát (TodoItem) és az ID típusát (Long)
// Ez automatikusan biztosítja az alap CRUD metódusokat: save(), findById(), findAll(), deleteById() stb.
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // Egyelőre nincs szükség egyedi lekérdezésekre, a JpaRepository mindent tud, ami kell.
}
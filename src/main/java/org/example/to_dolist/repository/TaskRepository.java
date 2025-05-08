package org.example.to_dolist.repository;

import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.User; // Importálni
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort; // Importálni Spring Data JPA sort támogatásához

import java.util.List; // Importálni
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // ÚJ metódus: Feladatok keresése felhasználó ID alapján, rendezéssel
    List<Task> findByUserId(UUID userId, Sort sort);

    // ÚJ metódus: Feladatok keresése, amikhez nincs felhasználó rendelve, rendezéssel
    List<Task> findByUserIsNull(Sort sort);

    // findByUserId és findByUserIsNull metódusokat a Service-ben fogjuk használni.
    // Az alap JpaRepository findAll(Sort sort) metódusa továbbra is használható az összes feladat lekérdezéséhez.
}
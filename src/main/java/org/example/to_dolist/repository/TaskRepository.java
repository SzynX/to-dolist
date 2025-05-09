package org.example.to_dolist.repository;

import org.example.to_dolist.domain.Task;
import org.example.to_dolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Feladatok keresése felhasználó ID és archivált státusz alapján, rendezéssel
    List<Task> findByUserIdAndArchived(UUID userId, boolean archived, Sort sort);

    // Feladatok keresése, amikhez nincs felhasználó rendelve ÉS archivált státusz alapján, rendezéssel
    List<Task> findByUserIsNullAndArchived(boolean archived, Sort sort);

    // Minden feladat keresése archivált státusz alapján, rendezéssel
    List<Task> findByArchived(boolean archived, Sort sort);

    // NOTE: Az eredeti findByUserId(UUID userId, Sort sort) és findByUserIsNull(Sort sort)
    // metódusokat valószínűleg már nem fogjuk közvetlenül használni a TaskService-ben,
    // mivel mostantól mindig figyelembe vesszük az archivált státuszt.
    // De a Service-ben lévő logika eldönti, melyik újat hívja.
}
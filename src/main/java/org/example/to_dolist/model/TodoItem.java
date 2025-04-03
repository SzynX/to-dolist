package org.example.to_dolist.model; // Ellenőrizd a csomagnevet!

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // Jelzi, hogy ez egy adatbázis táblának megfelelő entitás
@Data   // Lombok: getterek, setterek, toString, equals, hashCode generálása
@NoArgsConstructor // Lombok: Üres konstruktor (JPA-nak kell)
@AllArgsConstructor // Lombok: Minden mezős konstruktor
public class TodoItem {

    @Id // Ez az elsődleges kulcs
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatikusan növekvő ID (adatbázis generálja)
    private Long id;

    private String task; // A teendő leírása

    private boolean completed = false; // Alapértelmezetten nincs kész

    // Ide később vehetsz fel új mezőket, pl.:
    // private java.time.LocalDate dueDate;
    // private int priority;
}
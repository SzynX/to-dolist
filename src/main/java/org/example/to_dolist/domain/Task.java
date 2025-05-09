package org.example.to_dolist.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private boolean completed;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ÚJ mező az archiváláshoz, alapértelmezett értéke false
    private boolean archived = false;


    // Enum a feladat státuszokhoz
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        OVERDUE
    }

    // Enum a feladat prioritásokhoz
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }


    // A hátralévő napok számítása (maradhat)
    public long getDaysUntilDueDate() {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // Vizuális figyelmeztetés (valószínűleg már nem lesz használva)
    public String getDueDateWarning() {
        if (completed || archived) { // Ha kész vagy archiválva, nincs figyelmeztetés
            return null;
        }
        long daysLeft = getDaysUntilDueDate();
        if (daysLeft == 1) {
            return "⏰ 1 nap van hátra!";
        }
        return null;
    }
}
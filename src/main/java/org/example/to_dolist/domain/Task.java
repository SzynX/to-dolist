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

    // Új prioritás mező hozzáadása
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // A hátralévő napok számítása
    public long getDaysUntilDueDate() {
        if (dueDate == null) {
            return Long.MAX_VALUE; // Ha nincs határidő, ne jelenjen meg
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // Vizuális figyelmeztetés, ha a határidő közel van
    public String getDueDateWarning() {
        long daysLeft = getDaysUntilDueDate();
        if (daysLeft > 1) {
            return null; // Nincs közvetlen figyelmeztetés
        } else if (daysLeft == 1) {
            return "⏰ 1 nap van hátra!"; // 1 napos figyelmeztetés
        } else if (daysLeft <= 0) {
            return "⏰ Lejárt a határidő!"; // Lejárt a határidő
        }
        return null;
    }
}

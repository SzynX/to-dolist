package org.example.to_dolist.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "app_user") // 👈 Most már van táblanév!
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Az AUTO használata, mivel H2-ben nem minden esetben működik a UUID típusú generálás
    private UUID id;

    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;
}

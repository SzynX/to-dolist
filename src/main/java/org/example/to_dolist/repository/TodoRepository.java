package org.example.to_dolist.repository;

import org.example.to_dolist.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {

}
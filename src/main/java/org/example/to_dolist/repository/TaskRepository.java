package org.example.to_dolist.repository;

import org.example.to_dolist.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {


    List<Task> findByUserIdAndArchived(
            UUID userId, boolean archived, Sort sort);


    List<Task> findByUserIsNullAndArchived(boolean archived, Sort sort);


    List<Task> findByArchived(boolean archived, Sort sort);

}
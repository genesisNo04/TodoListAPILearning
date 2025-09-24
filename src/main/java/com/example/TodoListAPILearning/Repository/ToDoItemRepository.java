package com.example.TodoListAPILearning.Repository;

import com.example.TodoListAPILearning.Model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Long> {

    Optional<ToDoItem> findByTitle(String title);
}

package com.example.TodoListAPILearning.Repository;

import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByDisplayName(String displayName);

    void deleteByDisplayName(String displayName);
}

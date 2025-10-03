package com.example.TodoListAPILearning.Service;

import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;

import java.util.List;

public interface ToDoItemService {

    ToDoItem findByTitle(String title);

    ToDoItem findById(Long id);

    ToDoItem saveToDoItem(ToDoItem toDoItem);

    String deleteToDoItem(Long id);

    List<ToDoItem> findToDoItemByDisplayName(String displayName);

    List<ToDoItem> findByAppUser(AppUser appUser);

    void deleteItemByTitle(String title);

    boolean existByTitle(String title);

    boolean existById(Long id);
}

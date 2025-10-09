package com.example.TodoListAPILearning.Service;

import com.example.TodoListAPILearning.DTO.ToDoItemDTO;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    //new method to add pagination
    Page<ToDoItem> findToDoItemByDisplayNameWithPagination(String displayName,
                                                           int page,
                                                           int limit,
                                                           String sortCriteria,
                                                           boolean asc);

    public Page<ToDoItem> findToDoItemByFilters(String displayName, int page, int limit,
                                                String sortType, boolean asc,
                                                String title, String description);
}

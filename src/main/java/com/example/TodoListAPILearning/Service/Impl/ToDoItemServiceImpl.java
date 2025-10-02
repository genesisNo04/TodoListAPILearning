package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.Exception.ResourceNotFoundException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import com.example.TodoListAPILearning.Repository.ToDoItemRepository;
import com.example.TodoListAPILearning.Repository.AuthUserRepository;
import com.example.TodoListAPILearning.Service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoItemServiceImpl implements ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Autowired
    private AuthUserRepository userRepository;

    @Override
    public ToDoItem findByTitle(String title) {
        return toDoItemRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("No to do item with title: " + title));
    }

    @Override
    public ToDoItem findById(Long id) {
        return toDoItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No to do item with id: " + id));
    }

    @Override
    public ToDoItem saveToDoItem(ToDoItem toDoItem) {
        return toDoItemRepository.save(toDoItem);
    }

    @Override
    public String deleteToDoItem(Long id) {
        if (!toDoItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("No to do item with id: " + id);
        }

        toDoItemRepository.deleteById(id);
        return "Todo item has been deleted";
    }

    @Override
    public List<ToDoItem> findToDoItemByDisplayName(String displayName) {
        return toDoItemRepository.findByAppUserDisplayName(displayName);
    }

    @Override
    public List<ToDoItem> findByAppUser(AppUser appUser) {
        return toDoItemRepository.findByAppUser(appUser);
    }
}

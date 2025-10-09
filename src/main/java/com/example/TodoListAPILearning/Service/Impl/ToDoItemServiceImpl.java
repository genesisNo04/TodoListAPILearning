package com.example.TodoListAPILearning.Service.Impl;

import com.example.TodoListAPILearning.Exception.ResourceNotFoundException;
import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import com.example.TodoListAPILearning.Repository.ToDoItemRepository;
import com.example.TodoListAPILearning.Repository.AuthUserRepository;
import com.example.TodoListAPILearning.Service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    //Page: represents a slice of data (a single page) from a lerger result set
    //Has metadata like total count, total pages
    //When with pagination, spring return Page<T> object => get both data and page info
    @Override
    public Page<ToDoItem> findToDoItemByDisplayNameWithPagination(String displayName, int page, int limit, String sortType, boolean asc) {
        // return page will contain: List<ToDoItem> items = page.getContent(); // actual list of ToDoItem
        /*
        *   int currentPage = page.getNumber();        // current page index (0-based)
            int totalPages = page.getTotalPages();     // total number of pages
            long totalElements = page.getTotalElements(); // total number of items in DB
            int pageSize = page.getSize();             // number of items per page
            boolean hasNext = page.hasNext();          // true if next page exists
            boolean hasPrevious = page.hasPrevious();  // true if previous page exists
         */

        //Pageable is an interface in spring data JPA represent JPA pagination
        //Instruction for which slice(page) of data want from the db
        // Tell spring i want page number x, with Y items per page, optionally sorted in this order
        //Page<T> is the result (the data get back)
        //Pageable is the query input (how you ask for it)
        Pageable pageable = null;
        if (asc) {
            pageable = PageRequest.of(page - 1, limit, Sort.by(sortType).ascending());
        } else {
            pageable = PageRequest.of(page - 1, limit, Sort.by(sortType).descending());
        }

        return toDoItemRepository.findByAppUser_DisplayName(displayName, pageable);
    }

    @Override
    public List<ToDoItem> findByAppUser(AppUser appUser) {
        return toDoItemRepository.findByAppUser(appUser);
    }

    @Override
    public void deleteItemByTitle(String title) {
        toDoItemRepository.deleteByTitle(title);
    }

    @Override
    public boolean existByTitle(String title) {
        return toDoItemRepository.existsByTitle(title);
    }

    @Override
    public boolean existById(Long id) {
        return toDoItemRepository.existsById(id);
    }

    @Override
    public List<ToDoItem> findToDoItemByDisplayName(String displayName) {
        return toDoItemRepository.findByAppUserDisplayName(displayName);
    }


    @Override
    public Page<ToDoItem> findToDoItemByFilters(String displayName, int page, int limit, String sortType, boolean asc, String title, String description) {
        Sort sort = asc ? Sort.by(sortType).ascending() : Sort.by(sortType).descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        return toDoItemRepository.findByFilters(displayName, title, description, pageable);
    }
}

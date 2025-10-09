package com.example.TodoListAPILearning.Repository;

import com.example.TodoListAPILearning.Model.AppUser;
import com.example.TodoListAPILearning.Model.ToDoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Long> {

    Optional<ToDoItem> findByTitle(String title);

    List<ToDoItem> findByAppUserDisplayName(String username);

    boolean existsById(Long id);

    boolean existsByTitle(String title);

    List<ToDoItem> findByAppUser(AppUser appUser);

    void deleteByTitle(String title);

    // this will create a query like: SELECT * FROM todo_item WHERE app_user_display_name = 'John' LIMIT 10 OFFSET 10;
    Page<ToDoItem> findByAppUser_DisplayName(String displayName, Pageable pageable);

    @Query("""
       SELECT t FROM ToDoItem t
       WHERE t.appUser.displayName = :displayName
         AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
         AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%')))
       """)
    Page<ToDoItem> findByFilters(@Param("displayName") String displayName,
                                 @Param("title") String title,
                                 @Param("description") String description,
                                 Pageable pageable);

}

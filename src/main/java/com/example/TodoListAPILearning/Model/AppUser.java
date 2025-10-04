package com.example.TodoListAPILearning.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "auth_user_id", nullable = false)
    @JsonManagedReference
    private AuthUser authUser;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<ToDoItem> toDoItemList;

    public AppUser() {
    }

    public AppUser(String displayName, AuthUser authUser, List<ToDoItem> toDoItemList) {
        this.displayName = displayName;
        this.authUser = authUser;
        this.toDoItemList = toDoItemList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    public List<ToDoItem> getToDoItemList() {
        return toDoItemList;
    }

    public void setToDoItemList(List<ToDoItem> toDoItemList) {
        this.toDoItemList = toDoItemList;
    }
}

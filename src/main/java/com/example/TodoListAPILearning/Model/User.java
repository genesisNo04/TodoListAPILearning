package com.example.TodoListAPILearning.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_todo",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_id")
    )
    private List<ToDoItem> toDoItemList;

    public User(String username, String password, String email, List<ToDoItem> toDoItemList) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.toDoItemList = toDoItemList;
    }
}

package com.example.TodoListAPILearning.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "to_do_item")
@NoArgsConstructor
@Getter
@Setter
public class ToDoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private AppUser appUser;

    public ToDoItem(String title, String description, AppUser appUser) {
        this.title = title;
        this.description = description;
        this.appUser = appUser;
    }
}

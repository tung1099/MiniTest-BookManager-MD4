package com.codegym.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(targetEntity = Book.class)
    private List<Book> books;

    public Category() {
    }

    public Category(Long id, String name, String description, List<Book> books) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.books = books;
    }

    public Category(String name, String description, List<Book> books) {
        this.name = name;
        this.description = description;
        this.books = books;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}

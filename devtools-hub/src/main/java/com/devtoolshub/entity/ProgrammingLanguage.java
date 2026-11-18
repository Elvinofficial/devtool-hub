package com.devtoolshub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "programming_languages")
public class ProgrammingLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected ProgrammingLanguage() {
    }

    public ProgrammingLanguage(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

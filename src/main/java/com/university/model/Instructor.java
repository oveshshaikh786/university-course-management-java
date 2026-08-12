package com.university.model;

import java.util.UUID;

public class Instructor {

    private final UUID id;
    private String name;
    private String email;

    public Instructor(String name, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Instructor{name='" + name + "', email='" + email + "'}";
    }
}

package com.example.android_movie.ui.producer;

public class Producer {
    String name, email, id;

    public Producer(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return "Producer{" +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                "id='" + id + '\'' +
                '}';
    }
}
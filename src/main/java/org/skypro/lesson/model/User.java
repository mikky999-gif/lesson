package org.skypro.lesson.model;

import java.util.UUID;

public class User {
    private final UUID id;

    public User(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
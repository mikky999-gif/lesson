package org.skypro.lesson.model;

import java.util.UUID;

public class Product {
    private String name;
    private final String id;
    private String rules;
    private String description;

    public Product(String name, String id, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }

    public Product(String name, String description) {
        this(name, UUID.randomUUID().toString(), description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }
}

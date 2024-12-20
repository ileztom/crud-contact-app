package com.contactapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Contact {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Конструктор
    public Contact(UUID id, String name, String phone, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Геттеры
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Сеттеры
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
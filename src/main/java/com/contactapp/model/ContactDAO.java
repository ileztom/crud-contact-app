package com.contactapp.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContactDAO {
    private Connection connection;

    public ContactDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS contacts (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "phone TEXT," +
                    "email TEXT," +
                    "createdAt TEXT," +
                    "updatedAt TEXT" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Создание контакта
    public void createContact(Contact contact) throws SQLException {
        String sql = "INSERT INTO contacts (id, name, phone, email, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contact.getId().toString());
            stmt.setString(2, contact.getName());
            stmt.setString(3, contact.getPhone());
            stmt.setString(4, contact.getEmail());
            stmt.setString(5, contact.getCreatedAt().toString());
            stmt.setString(6, contact.getUpdatedAt().toString());
            stmt.executeUpdate();
        }
    }

    // Получение всех контактов
    public List<Contact> getAllContacts() throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contact contact = new Contact(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        LocalDateTime.parse(rs.getString("createdAt")),
                        LocalDateTime.parse(rs.getString("updatedAt"))
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    // Обновление контакта
    public void updateContact(Contact contact) throws SQLException {
        String sql = "UPDATE contacts SET name = ?, phone = ?, email = ?, updatedAt = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getPhone());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, contact.getId().toString());
            stmt.executeUpdate();
        }
    }

    // Удаление контакта
    public void deleteContact(UUID id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }
    }
}
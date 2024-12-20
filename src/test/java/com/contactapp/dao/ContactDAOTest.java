package com.contactapp.dao;

import com.contactapp.model.Contact;
import com.contactapp.model.ContactDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContactDAOTest {
    private Connection connection;
    private ContactDAO contactDAO;

    @BeforeEach
    public void setUp() throws SQLException, ClassNotFoundException {
        // Загрузка драйвера SQLite
        Class.forName("org.sqlite.JDBC");

        // Подключение к тестовой базе данных
        connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        contactDAO = new ContactDAO(connection);

        // Очистка таблицы перед каждым тестом
        connection.createStatement().execute("DELETE FROM contacts");
    }

    @Test
    public void testCRUDOperations() throws SQLException {
        // Создание контакта
        Contact contact = new Contact(
                UUID.randomUUID(),
                "Иван Иванов",
                "+79991234567",
                "ivan@example.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        contactDAO.createContact(contact);

        // Проверка чтения
        List<Contact> contacts = contactDAO.getAllContacts();
        assertEquals(1, contacts.size());
        assertEquals("Иван Иванов", contacts.get(0).getName());

        // Обновление контакта
        contact.setPhone("+79997654321");
        contactDAO.updateContact(contact);

        contacts = contactDAO.getAllContacts();
        assertEquals("+79997654321", contacts.get(0).getPhone());

        // Удаление контакта
        contactDAO.deleteContact(contact.getId());
        contacts = contactDAO.getAllContacts();
        assertEquals(0, contacts.size());
    }
}
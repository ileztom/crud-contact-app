package com.contactapp.controller;

import com.contactapp.model.Contact;
import com.contactapp.model.ContactDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ContactController {
    private ContactDAO contactDAO;

    public ContactController() {
        try {
            // Подключение к базе данных
            Connection connection = DriverManager.getConnection("jdbc:sqlite:contacts.db");
            contactDAO = new ContactDAO(connection);

            // Проверка загрузки драйвера SQLite
            Class.forName("org.sqlite.JDBC");
            System.out.println("Драйвер SQLite загружен успешно.");

            // Проверка существования таблицы
            boolean tableExists = checkIfTableExists(connection, "contacts");
            if (!tableExists) {
                // Чтение SQL-скрипта из файла
                String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/db/schema.sql")));

                // Выполнение SQL-скрипта
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
            } else {
                System.out.println("Таблица 'contacts' уже существует.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Драйвер SQLite не найден: " + e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Ошибка подключения к базе данных: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Ошибка чтения SQL-скрипта: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Метод для проверки существования таблицы
    private boolean checkIfTableExists(Connection connection, String tableName) throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Если есть результат, таблица существует
            }
        }
    }

    // Метод для добавления контакта с проверкой ввода
    public void addContact() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить контакт");
        dialog.setHeaderText("Введите данные контакта");
        dialog.setContentText("Имя:");

        Optional<String> nameResult = dialog.showAndWait();
        if (nameResult.isPresent()) {
            String name = nameResult.get();

            dialog.setContentText("Телефон:");
            Optional<String> phoneResult = dialog.showAndWait();
            String phone = phoneResult.orElse("");

            // Проверка ввода телефона
            if (!isValidPhone(phone)) {
                Alert alert = new Alert(AlertType.WARNING, "Некорректный телефон. Введите только цифры и символы +, -, или пробелы.");
                alert.showAndWait();
                return; // Прерываем добавление контакта
            }

            dialog.setContentText("Email:");
            Optional<String> emailResult = dialog.showAndWait();
            String email = emailResult.orElse("");

            Contact contact = new Contact(
                    UUID.randomUUID(),
                    name,
                    phone,
                    email,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            try {
                contactDAO.createContact(contact);
                Alert alert = new Alert(AlertType.INFORMATION, "Контакт добавлен");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Ошибка при добавлении контакта");
                alert.showAndWait();
            }
        }
    }

    // Метод для проверки корректности телефона
    private boolean isValidPhone(String phone) {
        // Регулярное выражение для проверки телефона
        String phonePattern = "^[0-9+\\- ]+$"; // Разрешены цифры, +, - и пробелы
        return phone.matches(phonePattern);
    }

    // Метод для редактирования контакта
    public void editContact(Contact contact) {
        if (contact == null) {
            Alert alert = new Alert(AlertType.WARNING, "Выберите контакт для редактирования");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(contact.getName());
        dialog.setTitle("Редактировать контакт");
        dialog.setHeaderText("Измените данные контакта");
        dialog.setContentText("Имя:");

        Optional<String> nameResult = dialog.showAndWait();
        if (nameResult.isPresent()) {
            String name = nameResult.get();

            dialog.setContentText("Телефон:");
            Optional<String> phoneResult = dialog.showAndWait();
            String phone = phoneResult.orElse(contact.getPhone());

            // Проверка ввода телефона
            if (!isValidPhone(phone)) {
                Alert alert = new Alert(AlertType.WARNING, "Некорректный телефон. Введите только цифры и символы +, -, или пробелы.");
                alert.showAndWait();
                return; // Прерываем редактирование контакта
            }

            dialog.setContentText("Email:");
            Optional<String> emailResult = dialog.showAndWait();
            String email = emailResult.orElse(contact.getEmail());

            contact.setName(name);
            contact.setPhone(phone);
            contact.setEmail(email);
            contact.setUpdatedAt(LocalDateTime.now());

            try {
                contactDAO.updateContact(contact);
                Alert alert = new Alert(AlertType.INFORMATION, "Контакт обновлён");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, "Ошибка при обновлении контакта");
                alert.showAndWait();
            }
        }
    }

    // Метод для удаления контакта
    public void deleteContact(Contact contact) {
        if (contact == null) {
            Alert alert = new Alert(AlertType.WARNING, "Выберите контакт для удаления");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION, "Вы уверены, что хотите удалить контакт?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contactDAO.deleteContact(contact.getId());
                Alert infoAlert = new Alert(AlertType.INFORMATION, "Контакт удалён");
                infoAlert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(AlertType.ERROR, "Ошибка при удалении контакта");
                errorAlert.showAndWait();
            }
        }
    }

    // Метод для получения всех контактов
    public List<Contact> getAllContacts() {
        try {
            return contactDAO.getAllContacts();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Ошибка при получении контактов: " + e.getMessage());
            alert.showAndWait();
            return new ArrayList<>(); // Возвращаем пустой список в случае ошибки
        }
    }
}
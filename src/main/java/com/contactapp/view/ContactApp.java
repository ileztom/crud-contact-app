package com.contactapp.view;

import com.contactapp.controller.ContactController;
import com.contactapp.model.Contact;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ContactApp extends Application {
    private ContactController controller;
    private List<Contact> originalContactList; // Исходный список контактов

    @Override
    public void start(Stage primaryStage) {
        // Инициализация контроллера
        controller = new ContactController();

        // Создание GUI
        VBox root = new VBox();
        ListView<Contact> contactList = new ListView<>();
        Button addButton = new Button("Добавить");
        Button editButton = new Button("Редактировать");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить список");
        Button filterButton = new Button("Фильтр по алфавиту"); // Кнопка для фильтрации
        Button resetFilterButton = new Button("Сбросить фильтр"); // Кнопка для сброса фильтра

        // Обработка событий
        addButton.setOnAction(event -> controller.addContact());
        editButton.setOnAction(event -> controller.editContact(contactList.getSelectionModel().getSelectedItem()));
        deleteButton.setOnAction(event -> controller.deleteContact(contactList.getSelectionModel().getSelectedItem()));
        refreshButton.setOnAction(event -> {
            // Обновляем список контактов
            originalContactList = controller.getAllContacts(); // Сохраняем исходный список
            contactList.getItems().setAll(originalContactList);
        });

        // Логика фильтрации по алфавиту
        filterButton.setOnAction(event -> {
            List<Contact> filteredList = originalContactList.stream()
                    .sorted(Comparator.comparing(Contact::getName))
                    .collect(Collectors.toList());
            contactList.getItems().setAll(filteredList);
        });

        // Логика сброса фильтра
        resetFilterButton.setOnAction(event -> {
            contactList.getItems().setAll(originalContactList);
        });

        // Инициализация списка контактов
        originalContactList = controller.getAllContacts(); // Сохраняем исходный список
        contactList.getItems().setAll(originalContactList);

        // Установка отображения контактов в ListView
        contactList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                } else {
                    setText(contact.getName() + " (" + contact.getPhone() + ")");
                }
            }
        });

        root.getChildren().addAll(contactList, addButton, editButton, deleteButton, refreshButton, filterButton, resetFilterButton);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Управление контактами");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
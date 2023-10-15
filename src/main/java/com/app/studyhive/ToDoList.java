package com.app.studyhive;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.sql.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class ToDoList {

    static Connection con;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive","root", "kxn_2004");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Label label;

    @FXML
    private MFXButton add;

    @FXML
    private MFXButton remove;

    @FXML
    private MFXButton edit;

    @FXML
    private MFXButton save;

    @FXML
    private ListView<String> tasks;

    @FXML
    private MFXTextField text;

    boolean isTaskEmpty() {
        label.setText("");
        if (text.getText().isEmpty()) {
            System.out.println("The Task content is Empty!");
            label.setText("The Task content is Empty!");
            return true;
        } return false;
    }

    boolean isListEmpty() {
        label.setText("");
        if (tasks.getItems().isEmpty()) {
            System.out.println("The ToDo List is Empty!");
            label.setText("Cannot delete from empty list!");
            return true;
        } return false;
    }

    boolean taskSelected() {
        label.setText("");
        if (tasks.getSelectionModel().getSelectedIndex() == -1) {
            System.out.println("No selected item");
            label.setText("No Selected item!");
            return false;
        } return true;
    }

    @FXML
    void addTask() {
        if (!isTaskEmpty()) { tasks.getItems().add(text.getText()); };
    }

    @FXML
    void deleteTask() {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isListEmpty() && taskSelected()) { tasks.getItems().remove(index); }
    }

    @FXML
    void editTask() {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isTaskEmpty() && taskSelected()) { tasks.getItems().set(index, text.getText()); }
    }

    @FXML
    void save() {
        System.out.println(tasks.getItems());
    }

}

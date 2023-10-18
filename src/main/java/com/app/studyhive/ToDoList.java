package com.app.studyhive;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import static java.lang.Thread.sleep;


public class ToDoList implements Initializable {

    static Connection con;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive", "root", System.getenv("DB_PASSWORD"));
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

    @FXML
    private ListView<String> calendarEvents;

    boolean isTaskPresent(String string) {
        label.setText("");
        ObservableList<String> taskList = tasks.getItems();
        if (taskList.contains(string)) {
            label.setText("Task already present");
            return true;
        } return false;
    }

    boolean isTextEmpty() {
        label.setText("");
        if (text.getText().isEmpty()) {
            label.setText("The Task content is Empty!");
            return true;
        } return false;
    }

    boolean isListEmpty() {
        label.setText("");
        if (tasks.getItems().isEmpty()) {
            label.setText("Cannot delete from empty list!");
            return true;
        } return false;
    }

    boolean isTaskSelected() {
        label.setText("");
        if (tasks.getSelectionModel().getSelectedIndex() == -1) {
            label.setText("No Selected item!");
            return false;
        } return true;
    }

    @FXML
    void addTask() throws SQLException {
        String tempText = text.getText();
        if (!isTextEmpty() && !isTaskPresent(tempText)) {
            tasks.getItems().add(tempText);
            con.createStatement().execute("insert into StudyHive.ToDo(Task) value (\"" + tempText + "\");");
        }
    }

    @FXML
    void deleteTask() throws SQLException {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isListEmpty() && isTaskSelected()) {
            String task = tasks.getItems().get(index);
            tasks.getItems().remove(index);
            con.createStatement().execute("delete from StudyHive.ToDo where Task = \"" + task + "\";");
        }
    }

    @FXML
    void editTask() throws SQLException {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isTextEmpty() && isTaskSelected()) {
            String toTask = text.getText();
            con.createStatement().execute("update StudyHive.ToDo set Task = \"" + toTask + "\" where Task = \"" + tasks.getItems().get(index) + "\";");
            tasks.getItems().set(index, toTask);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ResultSet rs = con.createStatement().executeQuery("select * from StudyHive.ToDo");
            while (rs.next())
                tasks.getItems().add(rs.getString("Task"));
            rs = con.createStatement().executeQuery("Select * from StudyHive.events");
            while (rs.next())
                calendarEvents.getItems().add(rs.getString("event"));
        } catch (SQLException ignore) {}
    }
}

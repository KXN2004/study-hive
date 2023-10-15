package com.app.studyhive;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


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

    boolean isTaskPresent(String string) {
        label.setText("");
        ObservableList<String> taskList = tasks.getItems();
        if (taskList.contains(string)) {
            System.out.println("Task already present");
            label.setText("Task already present");
            return true;
        } return false;
    }

    boolean isTextEmpty() {
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

    boolean isTaskSelected() {
        label.setText("");
        if (tasks.getSelectionModel().getSelectedIndex() == -1) {
            System.out.println("No selected item");
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
            System.out.printf("%d %s\n", index, task);
            con.createStatement().execute("delete from StudyHive.ToDo where Task = \"" + task + "\";");
        }
    }

    @FXML
    void editTask() throws SQLException {
        int index = tasks.getSelectionModel().getSelectedIndex();
        System.out.println(index);
        if (!isTextEmpty() && isTaskSelected()) {
            String toTask = text.getText();
            con.createStatement().execute("update StudyHive.ToDo set Task = \"" + toTask + "\" where Task = \"" + tasks.getItems().get(index) + "\";");
            tasks.getItems().set(index, toTask);
        }
    }

    @FXML
    void save() throws SQLException {
        System.out.println(tasks.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ResultSet rs = con.createStatement().executeQuery("select * from StudyHive.ToDo");
            while (rs.next())
                tasks.getItems().add(rs.getString("Task"));
        } catch (SQLException ignored) {}
    }
}

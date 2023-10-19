package com.app.studyhive;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;


public class ToDoList implements Initializable {

    static Connection con;

    private static int db_no;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive", "root", System.getenv("DB_PASSWORD"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Label welcome;

    @FXML
    private MFXButton logout;

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

    @FXML
    void exit(ActionEvent event) throws IOException, SQLException {
        con.createStatement().executeUpdate("update StudyHive.status set logged_in=false where user=\"" + db_no + "\";");
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setTitle("Dashboard");
        stage.show();
    }

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
            con.createStatement().execute("insert into StudyHive.user" + db_no  + "(Task) value (\"" + tempText + "\");");
        }
    }

    @FXML
    void deleteTask() throws SQLException {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isListEmpty() && isTaskSelected()) {
            String task = tasks.getItems().get(index);
            tasks.getItems().remove(index);
            con.createStatement().execute("delete from StudyHive.user" + db_no + " where Task = \"" + task + "\";");
        }
    }

    @FXML
    void editTask() throws SQLException {
        int index = tasks.getSelectionModel().getSelectedIndex();
        if (!isTextEmpty() && isTaskSelected()) {
            String toTask = text.getText();
            con.createStatement().execute("update StudyHive.user" + db_no + " set Task = \"" + toTask + "\" where Task = \"" + tasks.getItems().get(index) + "\";");
            tasks.getItems().set(index, toTask);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ResultSet rs =  con.createStatement().executeQuery("select user from StudyHive.status where logged_in=true");
            rs.next();
            db_no = rs.getInt("user");
            rs = con.createStatement().executeQuery("select Fname from StudyHive.users where reg_id=" + db_no);
            rs.next();
            welcome.setText("Welcome, " + rs.getString("fname"));
            rs = con.createStatement().executeQuery("select * from StudyHive.user" + db_no);
            while (rs.next())
                tasks.getItems().add(rs.getString("Task"));
            rs = con.createStatement().executeQuery("Select * from StudyHive.events");
            while (rs.next())
                calendarEvents.getItems().add(rs.getString("event"));
        } catch (SQLException ignore) {}
    }
}

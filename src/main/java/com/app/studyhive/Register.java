package com.app.studyhive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.List;
import java.util.Objects;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.mindrot.jbcrypt.BCrypt;


public class Register {

    private final Paint green = new Color(0, 1, 0, 1);

    @FXML
    private MFXDatePicker birthdate;

    @FXML
    private MFXPasswordField confirmPassword;

    @FXML
    private MFXTextField email;

    @FXML
    private MFXTextField fname;

    @FXML
    private MFXComboBox<String> gender;

    @FXML
    private MFXTextField lname;

    @FXML
    private Label prompt;

    @FXML
    private MFXPasswordField password;

    @FXML
    private MFXTextField username;

    @FXML
    private MFXTextField passField;

    @FXML
    private MFXTextField userField;

    @FXML
    private Label loginPrompt;

    static Connection con;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive","root", System.getenv("DB_PASSWORD"));
//            private int db = con.createStatement().executeQuery("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initializeGender() {
        gender.setItems(FXCollections.observableArrayList("Male", "Female"));
    }

    boolean verifyInput(String string, int sizeLimit) {
        if (string.isEmpty()) {
            prompt.setText("Some text field is empty!");
            return false;
        } else if (string.contains(" ")) {
            prompt.setText("Some text field contains whitespace!");
            return false;
        } else if (string.length() > sizeLimit) {
            prompt.setText("Some text field entry is too long!");
            return false;
        }
        return true;
    }

    boolean comparePasswords(String pass1, String pass2) {
        if (!pass1.equals(pass2))
            prompt.setText("Passwords don't match");
        return pass1.equals(pass2);
    }

    void switchScene(ActionEvent event, String name) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(name));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setTitle("Dashboard");
        stage.show();
    }

    @FXML
    void oauth(ActionEvent event) throws GeneralSecurityException, IOException {
        try {
            List<String> events = Google.signIn();
            con.createStatement().execute("truncate table StudyHive.events;");
            for (String x: events){
                String dml = String.format("insert into StudyHive.events value ('%s');", x);
                con.createStatement().executeUpdate(dml);
            }
            switchScene(event, "todo-list.fxml");
        } catch (IOException e) {
            prompt.setText("Google authorization cancelled!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setTitle("Dashboard");
        stage.show();
    }

    @FXML
    void switchToRegister(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("material.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setTitle("Dashboard");
        stage.show();
    }

    boolean verifyLogin(String string, int sizeLimit) {
        if (string.isEmpty()) {
            loginPrompt.setText("Some text field is empty!");
            return false;
        } else if (string.contains(" ")) {
            loginPrompt.setText("Some text field contains whitespace!");
            return false;
        } else if (string.length() > sizeLimit) {
            loginPrompt.setText("Some text field entry is too long!");
            return false;
        }
        return true;
    }

    @FXML
    void signIn(ActionEvent event) throws SQLException, IOException {
        if (verifyLogin(userField.getText(), 20) && verifyLogin(passField.getText(), 8)) {
            ResultSet resultSet = con.createStatement().executeQuery("select * from StudyHive.users where username = \"" + userField.getText() + "\"");
            if (!resultSet.next()) {
                loginPrompt.setText("Username doesn't exist");
            } else {
                if (BCrypt.checkpw(passField.getText(), resultSet.getString("Password"))) {
                    con.createStatement().executeUpdate("update StudyHive.status set logged_in=false;");
                    con.createStatement().executeUpdate("update StudyHive.status set logged_in=true where user=" + resultSet.getInt("reg_id"));
                    loginPrompt.setTextFill(green);
                    loginPrompt.setText("Login successful!");
                    switchScene(event, "todo-list.fxml");
                } else loginPrompt.setText("Incorrect password");
            }
        }
    }

    @FXML
    void signUp(ActionEvent event) throws SQLException {
        if (!(verifyInput(username.getText(), 20)
            && verifyInput(password.getText(), 8)
            && verifyInput(fname.getText(), 20)
            && verifyInput(lname.getText(), 20)
            && verifyInput(email.getText(), 40)
            && verifyInput(confirmPassword.getText(), 8))) {
            return;
        } else if (gender.getSelectedItem() == null) {
            prompt.setText("Select your gender");
            return;
        } else if (birthdate.getValue() == null) {
            prompt.setText("Select your birthdate");
            return;
        }
        if (comparePasswords(password.getText(), confirmPassword.getText())) {
            try {
                String query = String.format(
                    "insert into StudyHive.users (Username, Fname, Lname, Gender, Birthdate, Email, Password) " +
                    "value (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
                    username.getText(),
                    fname.getText(),
                    lname.getText(),
                    gender.getSelectedItem(),
                    birthdate.getValue(),
                    email.getText(),
                    BCrypt.hashpw(password.getText(), BCrypt.gensalt(16))
                );
                con.createStatement().execute(query);
                ResultSet rs = con.createStatement().executeQuery("select reg_id from StudyHive.users where Username=\"" + username.getText() + "\";");
                rs.next();
                con.createStatement().execute("create table user" + rs.getInt("reg_id") + "(Task varchar(50));");
                con.createStatement().executeUpdate("insert into StudyHive.status(user) value (" + rs.getInt("reg_id") + ");");
                prompt.setTextFill(green);
                prompt.setText("Registration successful!");
            } catch (SQLIntegrityConstraintViolationException ignore) {
                prompt.setText("Student already exists!");
            }
        }
    }
}

package com.app.studyhive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.Objects;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
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

    static Connection con;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive","root", System.getenv("DB_PASSWORD"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initializeGender() {
        gender.setItems(FXCollections.observableArrayList("Male", "Female"));
    }

    boolean verifyInput(String string) {
        if (string.isEmpty()) {
            prompt.setText("Some text field is empty!");
            return false;
        } else if (string.contains(" ")) {
            prompt.setText("Some text field contains whitespace!");
            return false;
        } else if (string.length() > 9) {
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

    void switchScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("todo-list.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setTitle("ToDo List");
        stage.show();
    }

    @FXML
    void oauth(ActionEvent event) throws GeneralSecurityException, IOException {
        Google.signIn();
        switchScene(event);
    }

    @FXML
    void signIn(ActionEvent event) throws SQLException, IOException {
        if (verifyInput(username.getText()) && verifyInput(password.getText())) {
            ResultSet resultSet = con.createStatement().executeQuery("select * from StudyHive.users where username = \"" + username.getText() + "\"");
            if (!resultSet.next()) {
                prompt.setText("Username doesn't exist");
            } else {
                if (BCrypt.checkpw(password.getText(), resultSet.getString("Password"))) {
                    prompt.setTextFill(green);
                    prompt.setText("Login successful!");
                    switchScene(event);
                } else
                    prompt.setText("Incorrect password");
            }
        }
    }

    @FXML
    void signUp(ActionEvent event) throws SQLException {
        if (!(verifyInput(username.getText())
            && verifyInput(password.getText())
            && verifyInput(fname.getText())
            && verifyInput(lname.getText())
            && verifyInput(email.getText())
            && verifyInput(confirmPassword.getText()))) {
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
                        "insert into StudyHive.users values (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
                        username.getText(),
                        fname.getText(),
                        lname.getText(),
                        gender.getSelectedItem(),
                        birthdate.getValue(),
                        email.getText(),
                        BCrypt.hashpw(password.getText(), BCrypt.gensalt())
                );
                con.createStatement().execute(query);
            } catch (SQLIntegrityConstraintViolationException ignore) {
                prompt.setText("Student already exists!");
            }
        }
    }
}

package com.app.studyhive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Register {
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
            prompt.setText("Some field is empty!");
            return false;
        } else if (string.contains(" ")) {
            prompt.setText("Some field contains whitespace!");
            return false;
        } else if (string.length() > 9) {
            System.out.println(string.length());
            prompt.setText("Some field entry is too long!");
            return false;
        }
        return true;
    }

    boolean comparePasswords(String pass1, String pass2) {
        System.out.println(pass1 + " " + pass2);
        if (!pass1.equals(pass2))
            prompt.setText("Passwords don't match");
        return pass1.equals(pass2);
    }

    @FXML
    void oauth(ActionEvent event) throws GeneralSecurityException, IOException {
        Google.signIn();
    }

    @FXML
    void signIn(ActionEvent event) throws SQLException {
        if (verifyInput(username.getText()) && verifyInput(password.getText())) {
            ResultSet resultSet = con.createStatement().executeQuery("select * from StudyHive.users where username = \"" + username.getText() + "\"");
            if (!resultSet.next()) {
                prompt.setText("Username doesn't exist");
            } else {
                if (BCrypt.checkpw(password.getText(), resultSet.getString("Password")))
                    prompt.setText("Login successful!");
                else
                    prompt.setText("Incorrect password");
            }
        }
    }

    @FXML
    void signUp(ActionEvent event) throws SQLException {
        boolean proceed = verifyInput(username.getText())
            && verifyInput(password.getText())
            && verifyInput(fname.getText())
            && verifyInput(lname.getText())
            && verifyInput(email.getText())
            && verifyInput(confirmPassword.getText());
        boolean match = comparePasswords(password.getText(), confirmPassword.getText());
        if (proceed && match) {
            try {
                String hash = BCrypt.hashpw(password.getText(), BCrypt.gensalt(16));
                String query = String.format(
                        "insert into StudyHive.users values (\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\")",
                        username.getText(),
                        fname.getText(),
                        lname.getText(),
                        gender.getSelectedItem(),
                        birthdate.getSelectedText(),
                        email.getText(),
                        hash
                );
                con.createStatement().execute(query);
                prompt.setText("Registration successful!");
            } catch (SQLIntegrityConstraintViolationException ignore) {
                prompt.setText("Student already exists!");
            }
        }
    }
}

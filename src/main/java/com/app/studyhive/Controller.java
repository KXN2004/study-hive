package com.app.studyhive;

import java.sql.*;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    Label prompt;
    @FXML
    TextField userName;
    @FXML
    PasswordField passWord;
    static Connection con;

    static {
        try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive","root","kxn_2004");
                Statement stmt = con.createStatement();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    // Function to be called when the student wants to sign in
    @FXML
    void signIn() throws SQLException {
        System.out.println(verifyLogin(userName.getText().trim(), passWord.getText().trim()));
    }

    // Function which checks whether the user is in the database or not
    boolean verifyLogin(String username, String password) throws SQLException {
        ResultSet resultSet = con.createStatement().executeQuery("select * from StudyHive.credentials where username = \"" + username + "\"");
        if (!resultSet.next()) {
            prompt.setText("username doesn't exist");
            System.out.println("A student with this username doesn't exist!");
        } else {
            prompt.setText("Incorrect password");
            return Objects.equals(resultSet.getString(3), password);
        }
        return false;
    }

    // Function to be called when the students want to sign up
    @FXML
    void signUp() throws SQLException {
        String username = userName.getText();
        String password = passWord.getText();

        if (username.isEmpty() || password.isEmpty()) {
            prompt.setText("username/password is empty!");
            System.out.println("username or password is empty");
            return;
        } else if (username.contains(" ") || password.contains(" ")) {
            prompt.setText("field contains whitespace!");
            System.out.println("username or password contains whitespace");
            return;
        } else if (username.length() >= 20 || password.length() >= 20) {
            prompt.setText("username/password too long");
            System.out.println("username or password is too long (>= 20 characters)");
            return;
        }
        try {
            con.createStatement().executeUpdate("insert into credentials(username, password) value (\"" + username + "\", \"" + password + "\")");
            System.out.println("Added student to the database");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("A student with this username already exists!");
        }
    }
}
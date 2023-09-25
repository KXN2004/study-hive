package com.app.studyhive;

import java.sql.*;

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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    // Functions which checks whether the input is acceptable
    boolean verifyInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            prompt.setText("username/password is empty!");
            System.out.println("username or password is empty");
            return false;
        } else if (username.contains(" ") || password.contains(" ")) {
            prompt.setText("field contains whitespace!");
            System.out.println("username or password contains whitespace");
            return false;
        } else if (username.length() >= 20 || password.length() >= 20) {
            prompt.setText("username/password too long");
            System.out.println("username or password is too long (>= 20 characters)");
            return false;
        }
        return true;
    }

    // Function to be called when the student wants to sign in
    @FXML
    void signIn() throws SQLException {
        System.out.println(verifyLogin());
    }

    // Function which checks whether the user is in the database or not
    boolean verifyLogin() throws SQLException {
        if (verifyInput(userName.getText(), passWord.getText())) {
            ResultSet resultSet = con.createStatement().executeQuery("select * from StudyHive.credentials where username = \"" + userName.getText() + "\"");
            if (!resultSet.next()) {
                prompt.setText("username doesn't exist");
                System.out.println("A student with this username doesn't exist!");
            } else {
                if (BCrypt.checkpw(passWord.getText(), resultSet.getString(3)))
                    return true;
                else
                    prompt.setText("Incorrect password");
            }
        }
        return false;
    }

    // Function to be called when the students want to sign up
    @FXML
    void signUp() throws SQLException {
        if (verifyInput(userName.getText(), passWord.getText())) {
            try {
                String hash = BCrypt.hashpw(passWord.getText(), BCrypt.gensalt());
                con.createStatement().executeUpdate("insert into credentials(username, password) value (\"" + userName.getText() + "\", \"" + hash + "\")");
                prompt.setText("Signup Successful!");
                System.out.println("Added student to the database");
            } catch (SQLIntegrityConstraintViolationException e) {
                prompt.setText("student already exists");
                System.out.println("A student with this username already exists!");
            }
        }
    }

    // This main method is for trying out code inside of Controller Class
    public static void main(String[] args) {
        String hashed = BCrypt.hashpw("kxn_2004", BCrypt.gensalt());
        System.out.println(BCrypt.gensalt() + " | " + hashed);
        if (BCrypt.checkpw("kxn_2004", hashed))
            System.out.println("It matches");
        else
            System.out.println("It does not match");
    }
}
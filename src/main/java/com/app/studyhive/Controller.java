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
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/StudyHive","root", System.getenv("DB_PASSWORD"));
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
        } else if (username.length() >= 8 || password.length() >= 8) {
            prompt.setText("username or password is too long");
            System.out.println("username or password is too long (>= 8 characters)");
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
                System.out.println(resultSet.getString(3));
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
        // If username and password are in an acceptable format
        if (verifyInput(userName.getText(), passWord.getText())) {
            try {
                // Generate hash for the password
                String hash = BCrypt.hashpw(passWord.getText(), BCrypt.gensalt());
                // Store the hash in the database
                con.createStatement().executeUpdate("insert into credentials(username, password) value (\"" + userName.getText() + "\", \"" + hash + "\")");
                // Display the prompt text
                prompt.setText("Signup successful!");
                // Display the output in the console
                System.out.println("Added student to the database");
            } catch (SQLIntegrityConstraintViolationException e) { // If the student is already registered
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
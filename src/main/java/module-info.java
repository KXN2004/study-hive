module com.app.studyhive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires activation;
    requires mail;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.json.jackson2;
    requires com.google.api.services.classroom;
    requires com.google.api.services.drive;
    requires com.google.api.services.tasks;
    requires jdk.httpserver;
    requires com.google.api.services.calendar;


    opens com.app.studyhive to javafx.fxml;
    exports com.app.studyhive;
}
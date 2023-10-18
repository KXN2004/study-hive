module com.app.studyhive {
    requires jbcrypt;
    requires MaterialFX;
    requires java.sql;
    requires jdk.httpserver;
    requires javafx.controls;
    requires javafx.fxml;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.calendar;
    requires com.google.api.services.tasks;
    requires com.google.api.services.drive;

    opens com.app.studyhive to javafx.fxml;
    exports com.app.studyhive;
}
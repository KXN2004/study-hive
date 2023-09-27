module com.app.studyhive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires activation;
    requires mail;


    opens com.app.studyhive to javafx.fxml;
    exports com.app.studyhive;
}
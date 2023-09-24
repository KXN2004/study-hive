module com.app.studyhive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.app.studyhive to javafx.fxml;
    exports com.app.studyhive;
}
module tracker.ofertas.laborales {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.tracker to javafx.fxml;
    exports com.example.tracker;
    exports com.example.tracker.controllers;
    opens com.example.tracker.controllers to javafx.fxml;

}
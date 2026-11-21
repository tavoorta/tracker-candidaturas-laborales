module tracker.ofertas.laborales {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.tracker to javafx.fxml;
    exports com.example.tracker;
    exports com.example.tracker.presentacion.controllers;
    opens com.example.tracker.presentacion.controllers to javafx.fxml;

}
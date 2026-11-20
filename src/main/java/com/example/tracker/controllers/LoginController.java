package com.example.tracker.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @javafx.fxml.FXML
    private PasswordField txtContrasena;

    @javafx.fxml.FXML
    private TextField txtCorreo;

    @javafx.fxml.FXML
    public void iniciarSesion(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/shell-view.fxml"));
            Parent shell = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(shell, 1200, 720));
            stage.setTitle("MentorIA");

        } catch (IOException excepcion) {
            throw new RuntimeException("No se pudo cargar shell-view.fxml", excepcion);
        }

    }
}

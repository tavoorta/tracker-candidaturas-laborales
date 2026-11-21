package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    //region FXML
    @javafx.fxml.FXML
    private PasswordField txtContrasena;
    @javafx.fxml.FXML
    private TextField txtCorreo;
    //endregion FXML

    @javafx.fxml.FXML
    public void iniciarSesion(ActionEvent actionEvent) {
        SceneNavigator.navigateToInjection("shell-view.fxml", "MentorIA");
    }
}

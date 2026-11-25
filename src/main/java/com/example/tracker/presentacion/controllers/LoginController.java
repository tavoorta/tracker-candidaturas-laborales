package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** Pantalla de inicio de sesión. */
public class LoginController {

    //region FXML
    @javafx.fxml.FXML
    private PasswordField txtContrasena;
    @javafx.fxml.FXML
    private TextField txtCorreo;
    //endregion FXML

    /**
     * Abre la ventana principal.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void iniciarSesion(ActionEvent actionEvent) {
        SceneNavigator.navigateToInjection("shell-view.fxml", "MentorIA");
    }
}

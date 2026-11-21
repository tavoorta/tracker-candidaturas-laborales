package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShellController implements Initializable {

    //region FXML
    @javafx.fxml.FXML
    private StackPane contenido;
    @javafx.fxml.FXML
    private Label btnDashboard;
    @javafx.fxml.FXML
    private Label lblRolUsuario;
    @javafx.fxml.FXML
    private Label lblNombreUsuario;
    @javafx.fxml.FXML
    private Label btnCandidaturas;
    @javafx.fxml.FXML
    private Label btnEmpresas;
    @javafx.fxml.FXML
    private Label lblIniciales;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarUsuarioActual();
        irDashboard(null);
    }

    @javafx.fxml.FXML
    public void irDashboard(MouseEvent event) {
        marcarBotonActivo(btnDashboard);
    }

    @javafx.fxml.FXML
    public void irCandidaturas(MouseEvent event) {
        cargarContenido("candidaturas-view.fxml");
        marcarBotonActivo(btnCandidaturas);
    }

    @javafx.fxml.FXML
    public void irEmpresas(MouseEvent event) {
        marcarBotonActivo(btnEmpresas);
    }

    @javafx.fxml.FXML
    public void cerrarSesion(MouseEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar sesión");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea salir?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            SceneNavigator.navigateToInjection("login-view.fxml", "MentorIA - Iniciar sesión");
        }
    }

    @javafx.fxml.FXML
    public void modoOscuro(ActionEvent actionEvent) {
    }

    private void cargarContenido(String nombreFxml) {
        SceneNavigator.navigateToContenido(contenido, nombreFxml);
    }

    private void marcarBotonActivo(Label botonActivo) {
        for (Label boton : new Label[]{btnDashboard, btnCandidaturas, btnEmpresas}) {
            boolean esElActivo = boton == botonActivo;
            boton.setStyle(esElActivo
                    ? "-fx-text-fill: white; -fx-background-color: #0F766E; -fx-padding: 12 16; -fx-cursor: hand;"
                    : "-fx-text-fill: white; -fx-background-radius: 0; -fx-padding: 12 16; -fx-cursor: hand;");
        }
    }

    private String calcularIniciales(String nombreCompleto) {
        String[] partes = nombreCompleto.trim().split("\\s+");
        String primeraInicial = partes.length > 0 ? partes[0].substring(0, 1) : "";
        String segundaInicial = partes.length > 1 ? partes[1].substring(0, 1) : "";
        return (primeraInicial + segundaInicial).toUpperCase();
    }

    //PENDIENTE MIGRAR A BASE DE DATOS EN V3
    private void cargarUsuarioActual() {
        String nombreUsuario = "Gustavo Romero";
        String rolUsuario = "Estudiante DAW";

        lblNombreUsuario.setText(nombreUsuario);
        lblRolUsuario.setText(rolUsuario);
        lblIniciales.setText(calcularIniciales(nombreUsuario));
    }
}

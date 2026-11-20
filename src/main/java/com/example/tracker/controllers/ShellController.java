package com.example.tracker.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ShellController {

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


    @javafx.fxml.FXML
    public void irDashboard(MouseEvent event) {
    }

    @javafx.fxml.FXML
    public void irCandidaturas(MouseEvent event) {
        cargarContenido("candidaturas-view.fxml");
    }

    @javafx.fxml.FXML
    public void irEmpresas(MouseEvent event) {
    }

    @javafx.fxml.FXML
    public void cerrarSesion(MouseEvent event) {
    }

    @javafx.fxml.FXML
    public void accionPendiente(ActionEvent actionEvent) {
    }

    private void cargarContenido(String nombreFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + nombreFxml));
            Node vista = loader.load();
            contenido.getChildren().setAll(vista);
        } catch (IOException excepcion) {
            throw new RuntimeException("No se pudo cargar " + nombreFxml, excepcion);
        }
    }

}

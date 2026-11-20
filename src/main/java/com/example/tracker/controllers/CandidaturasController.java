package com.example.tracker.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CandidaturasController {
    @javafx.fxml.FXML
    private TableColumn colFecha;
    @javafx.fxml.FXML
    private TableColumn colEstado;
    @javafx.fxml.FXML
    private TableView tablaCandidaturas;
    @javafx.fxml.FXML
    private TableColumn colEmpresa;
    @javafx.fxml.FXML
    private TableColumn colTitulo;
    @javafx.fxml.FXML
    private ComboBox comboEstado;
    @javafx.fxml.FXML
    private TableColumn colSalario;
    @javafx.fxml.FXML
    private Label lblResumen;
    @javafx.fxml.FXML
    private TableColumn colAcciones;
    @javafx.fxml.FXML
    private ComboBox comboEmpresa;

    @javafx.fxml.FXML
    public void accionPendiente(ActionEvent actionEvent) {
    }
}

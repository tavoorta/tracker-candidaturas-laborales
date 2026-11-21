package com.example.tracker.presentacion.controllers;

import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.OfertaCandidatura;
import com.example.tracker.presentacion.celdas.FabricaCeldaAcciones;
import com.example.tracker.presentacion.celdas.FabricaCeldaEstado;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CandidaturasController implements Initializable {

    private OfertaCandidatura servicio;

    public CandidaturasController(OfertaCandidatura servicio) {
        this.servicio = servicio;
    }

    //region FXML
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colFecha;
    @javafx.fxml.FXML
    private TableColumn<Oferta, EstadoOferta> colEstado;
    @javafx.fxml.FXML
    private TableView<Oferta> tablaCandidaturas;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colEmpresa;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colTitulo;
    @javafx.fxml.FXML
    private TextField txtBuscar;
    @javafx.fxml.FXML
    private ComboBox<String> comboEstado;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colSalario;
    @javafx.fxml.FXML
    private Label lblResumen;
    @javafx.fxml.FXML
    private TableColumn<Oferta, Void> colAcciones;
    @javafx.fxml.FXML
    private ComboBox<String> comboEmpresa;
    @javafx.fxml.FXML
    private Button btnAnterior;
    @javafx.fxml.FXML
    private Button btnSiguiente;
    @javafx.fxml.FXML
    private HBox contenedorPaginas;
    //endregion FXML

    private static final int TAMANO_PAGINA = 10;
    private static final String TODOS_LOS_ESTADOS = "Todos los estados";
    private static final String TODAS_LAS_EMPRESAS = "Todas las empresas";

    private List<Oferta> ofertas;
    private int paginaActual = 1;

    @javafx.fxml.FXML
    public void accionPendiente(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colEmpresa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmpresa().getNombre()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colEstado.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEstado()));
        colEstado.setCellFactory(new FabricaCeldaEstado());
        colAcciones.setCellFactory(new FabricaCeldaAcciones<>(this::accionPendiente));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaPostulacion().toString()));
        colSalario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSalario() + " €"));

        cargarFiltros();
        aplicarFiltros();
    }

    @javafx.fxml.FXML
    public void irPaginaAnterior(ActionEvent actionEvent) {
        mostrarPagina(paginaActual - 1);
    }

    @javafx.fxml.FXML
    public void irPaginaSiguiente(ActionEvent actionEvent) {
        mostrarPagina(paginaActual + 1);
    }

    //region Paginación
    private int totalPaginas() {
        return Math.max(1, (int) Math.ceil((double) ofertas.size() / TAMANO_PAGINA));
    }

    private void mostrarPagina(int pagina) {
        paginaActual = pagina;

        int desde = (pagina - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, ofertas.size());
        tablaCandidaturas.getItems().setAll(ofertas.subList(desde, hasta));

        lblResumen.setText(ofertas.isEmpty()
                ? "No hay candidaturas"
                : "Mostrando " + (desde + 1) + "-" + hasta + " de " + ofertas.size() + " candidaturas");

        btnAnterior.setDisable(paginaActual == 1);
        btnSiguiente.setDisable(paginaActual == totalPaginas());
        crearBotonesPaginas();
    }

    /** Crea un botón por página (1, 2, ...) y resalta la página actual. */
    private void crearBotonesPaginas() {
        contenedorPaginas.getChildren().clear();

        for (int i = 1; i <= totalPaginas(); i++) {
            int numero = i;
            Button boton = new Button(String.valueOf(numero));
            if (numero == paginaActual) {
                boton.setStyle("-fx-background-color: #0F766E; -fx-text-fill: white;");
            }
            boton.setOnAction(e -> mostrarPagina(numero));
            contenedorPaginas.getChildren().add(boton);
        }
    }
    //endregion Paginación

    //region Filtros

    /** Llenar los desplegables - Estado y Empresa */
    private void cargarFiltros() {
        comboEstado.getItems().add(TODOS_LOS_ESTADOS);
        for (EstadoOferta estado : EstadoOferta.values()) {
            comboEstado.getItems().add(estado.name());
        }
        comboEstado.getSelectionModel().selectFirst();

        comboEmpresa.getItems().add(TODAS_LAS_EMPRESAS);
        comboEmpresa.getItems().addAll(servicio.listarEmpresas());
        comboEmpresa.getSelectionModel().selectFirst();

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        comboEstado.setOnAction(e -> aplicarFiltros());
        comboEmpresa.setOnAction(e -> aplicarFiltros());
    }

    /** Pide al servicio las ofertas que cumplen los filtros */
    private void aplicarFiltros() {
        String estado = TODOS_LOS_ESTADOS.equals(comboEstado.getValue()) ? null : comboEstado.getValue();
        String empresa = TODAS_LAS_EMPRESAS.equals(comboEmpresa.getValue()) ? null : comboEmpresa.getValue();

        ofertas = servicio.filtrar(estado, empresa, txtBuscar.getText());
        mostrarPagina(1);
    }
    //endregion Filtros
}

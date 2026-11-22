package com.example.tracker.presentacion.controllers;

import com.example.tracker.common.AppContext;
import com.example.tracker.SceneNavigator;
import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioOferta;
import com.example.tracker.presentacion.celdas.CeldaAcciones;
import com.example.tracker.presentacion.celdas.CeldaEstado;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class CandidaturasController implements Initializable {

    private ServicioOferta servicio;

    public CandidaturasController(ServicioOferta servicio) {
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
    private Pagination paginacion;
    //endregion FXML

    private static final int TAMANO_PAGINA = 10;
    private static final String TODOS_LOS_ESTADOS = "Todos los estados";
    private static final String TODAS_LAS_EMPRESAS = "Todas las empresas";

    private List<Oferta> ofertas;
    private int paginaActual = 1;

    @javafx.fxml.FXML
    public void nuevaOferta(ActionEvent actionEvent) {
        AppContext.getInstance().getDatos().remove("oferta");
        SceneNavigator.abrirModal("formulario-oferta-view.fxml", "Nueva candidatura");
        cargarEmpresas();
        recargarTabla();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colEmpresa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmpresa().getNombre()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colEstado.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEstado()));
        colEstado.setCellFactory(columna -> new CeldaEstado(this::cambiarEstado));
        colAcciones.setCellFactory(columna -> new CeldaAcciones<>(this::verOferta, this::editarOferta, this::eliminarOferta));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaPostulacion().toString()));
        colSalario.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.of("es", "ES"), "%,.0f €", c.getValue().getSalario())));

        paginacion.currentPageIndexProperty().addListener((obs, anterior, nueva) -> mostrarPagina(nueva.intValue() + 1));

        cargarFiltros();
        aplicarFiltros();
    }

    private void cambiarEstado(ActionEvent actionEvent) {
        Oferta oferta = tablaCandidaturas.getSelectionModel().getSelectedItem();
        servicio.cambiarEstado(oferta, oferta.getEstado());
        recargarTabla();
    }

    private void eliminarOferta(ActionEvent actionEvent) {
        Oferta oferta = tablaCandidaturas.getSelectionModel().getSelectedItem();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar candidatura");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar \"" + oferta.getTitulo()
                + "\" de " + oferta.getEmpresa().getNombre() + "?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            servicio.borrar(oferta);
            cargarEmpresas();
            recargarTabla();
        }
    }

    /** Vuelve a aplicar los filtros sin salir de la página actual. */
    private void recargarTabla() {
        int pagina = paginaActual;
        aplicarFiltros();
        mostrarPagina(Math.min(pagina, totalPaginas()));
    }

    private void verOferta(ActionEvent actionEvent) {
    }

    private void editarOferta(ActionEvent actionEvent) {
        AppContext.getInstance().put("oferta", tablaCandidaturas.getSelectionModel().getSelectedItem());
        SceneNavigator.abrirModal("formulario-oferta-view.fxml", "Editar candidatura");
        cargarEmpresas();
        recargarTabla();
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

        paginacion.setPageCount(totalPaginas());
        paginacion.setCurrentPageIndex(pagina - 1);
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

        cargarEmpresas();

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        comboEstado.setOnAction(e -> aplicarFiltros());
        comboEmpresa.setOnAction(e -> aplicarFiltros());
    }

    /** Llena el desplegable de empresas y mantiene la elegida si sigue existiendo. */
    private void cargarEmpresas() {
        String seleccionada = comboEmpresa.getValue();

        comboEmpresa.getItems().setAll(TODAS_LAS_EMPRESAS);
        comboEmpresa.getItems().addAll(servicio.listarEmpresas());

        if (comboEmpresa.getItems().contains(seleccionada)) {
            comboEmpresa.setValue(seleccionada);
        } else {
            comboEmpresa.getSelectionModel().selectFirst();
        }
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

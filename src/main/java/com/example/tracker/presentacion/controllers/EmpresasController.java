package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import com.example.tracker.common.AppContext;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.negocio.ServicioEmpresa;
import com.example.tracker.presentacion.celdas.CeldaAcciones;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Pantalla de empresas: tabla con buscador y paginación, alta/edición en un
 * formulario modal y borrado.
 */
public class EmpresasController implements Initializable {

    private final ServicioEmpresa servicio;

    /**
     * @param servicio servicio de empresas
     */
    public EmpresasController(ServicioEmpresa servicio) {
        this.servicio = servicio;
    }

    //region FXML
    @javafx.fxml.FXML
    private TextField txtBuscar;
    @javafx.fxml.FXML
    private TableView<Empresa> tablaEmpresas;
    @javafx.fxml.FXML
    private TableColumn<Empresa, String> colNombre;
    @javafx.fxml.FXML
    private TableColumn<Empresa, String> colUbicacion;
    @javafx.fxml.FXML
    private TableColumn<Empresa, Number> colCandidaturas;
    @javafx.fxml.FXML
    private TableColumn<Empresa, String> colContactos;
    @javafx.fxml.FXML
    private TableColumn<Empresa, Void> colAcciones;
    @javafx.fxml.FXML
    private Label lblResumen;
    @javafx.fxml.FXML
    private Pagination paginacion;
    //endregion FXML

    private static final int TAMANO_PAGINA = 10;

    private List<Empresa> empresas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colUbicacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUbicacion()));
        colCandidaturas.setCellValueFactory(c -> new SimpleIntegerProperty(servicio.contarCandidaturas(c.getValue())));
        colAcciones.setCellFactory(columna -> new CeldaAcciones<>(this::editarEmpresa, this::eliminarEmpresa));

        paginacion.currentPageIndexProperty().addListener((obs, anterior, nueva) -> mostrarPagina(nueva.intValue() + 1));
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> cargarEmpresas());
        cargarEmpresas();

        tablaEmpresas.setSortPolicy(tabla -> {
            cargarEmpresas();
            return true;
        });
    }

    /**
     * Abre el formulario para crear una empresa y recarga la tabla al cerrarlo.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void nuevaEmpresa(ActionEvent actionEvent) {
        AppContext.getInstance().getDatos().remove("empresa");
        SceneNavigator.abrirModal("formulario-empresa-view.fxml", "Nueva empresa");
        cargarEmpresas();
    }

    private void editarEmpresa(ActionEvent actionEvent) {
        AppContext.getInstance().put("empresa", tablaEmpresas.getSelectionModel().getSelectedItem());
        SceneNavigator.abrirModal("formulario-empresa-view.fxml", "Editar empresa");
        cargarEmpresas();
    }

    private void eliminarEmpresa(ActionEvent actionEvent) {
        Empresa empresa = tablaEmpresas.getSelectionModel().getSelectedItem();
        int candidaturas = servicio.contarCandidaturas(empresa);

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar empresa");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que desea eliminar " + empresa.getNombre() + "?"
                + (candidaturas > 0 ? "\nTambién se eliminarán sus " + candidaturas
                + " candidaturas con sus entrevistas y notas." : ""));

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            servicio.borrar(empresa);
            cargarEmpresas();
        }
    }

    /** Pide al servicio las empresas que cumplen la búsqueda, las ordena y muestra la primera página. */
    private void cargarEmpresas() {
        empresas = servicio.listar(txtBuscar.getText());
        ordenarSegunColumna();
        mostrarPagina(1);
    }

    /** Ordena la lista completa por la columna pulsada. */
    private void ordenarSegunColumna() {
        if (tablaEmpresas.getSortOrder().isEmpty()) {
            return;
        }

        TableColumn<Empresa, ?> columna = tablaEmpresas.getSortOrder().get(0);
        boolean ascendente = columna.getSortType() == TableColumn.SortType.ASCENDING;

        String campo = "";
        if (columna == colNombre) {
            campo = "nombre";
        } else if (columna == colUbicacion) {
            campo = "ubicacion";
        } else if (columna == colCandidaturas) {
            campo = "candidaturas";
        }

        empresas = servicio.ordenar(empresas, campo, ascendente);
    }

    //region Paginación
    private int totalPaginas() {
        return Math.max(1, (int) Math.ceil((double) empresas.size() / TAMANO_PAGINA));
    }

    private void mostrarPagina(int pagina) {
        int desde = (pagina - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, empresas.size());
        tablaEmpresas.getItems().setAll(empresas.subList(desde, hasta));

        lblResumen.setText(empresas.isEmpty()
                ? "No hay empresas"
                : "Mostrando " + (desde + 1) + "-" + hasta + " de " + empresas.size() + " empresas");

        paginacion.setPageCount(totalPaginas());
        paginacion.setCurrentPageIndex(pagina - 1);
    }
    //endregion Paginación
}

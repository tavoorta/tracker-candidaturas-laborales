package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import com.example.tracker.common.AppContext;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioEntrevista;
import com.example.tracker.negocio.ServicioNota;
import com.example.tracker.presentacion.celdas.CeldaAcciones;
import com.example.tracker.presentacion.celdas.CeldaEstado;
import com.example.tracker.presentacion.celdas.CeldaResultado;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Detalle de la candidatura guardada en el {@link AppContext} con la clave "oferta":
 * datos de la oferta, notas e historial de entrevistas.
 */
public class DetalleOfertaController implements Initializable {

    private final ServicioEntrevista servicioEntrevista;
    private final ServicioNota servicioNota;

    private Oferta oferta;

    /**
     * @param servicioEntrevista servicio de entrevistas
     * @param servicioNota       servicio de notas
     */
    public DetalleOfertaController(ServicioEntrevista servicioEntrevista, ServicioNota servicioNota) {
        this.servicioEntrevista = servicioEntrevista;
        this.servicioNota = servicioNota;
    }

    //region FXML
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private Label lblEmpresa;
    @javafx.fxml.FXML
    private Label lblEstado;
    @javafx.fxml.FXML
    private Label lblFecha;
    @javafx.fxml.FXML
    private Label lblSalario;
    @javafx.fxml.FXML
    private Label lblEnlace;
    @javafx.fxml.FXML
    private Label lblDescripcion;
    @javafx.fxml.FXML
    private TextField txtNuevaNota;
    @javafx.fxml.FXML
    private TableView<Nota> tablaNotas;
    @javafx.fxml.FXML
    private TableColumn<Nota, String> colFechaNota;
    @javafx.fxml.FXML
    private TableColumn<Nota, String> colTextoNota;
    @javafx.fxml.FXML
    private TableColumn<Nota, Void> colAccionesNota;
    @javafx.fxml.FXML
    private TableView<Entrevista> tablaEntrevistas;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colFechaEntrevista;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colTipo;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, Estado> colResultado;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colNotasEntrevista;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, Void> colAccionesEntrevista;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        oferta = (Oferta) AppContext.getInstance().get("oferta");

        lblTitulo.setText(oferta.getTitulo());
        lblEmpresa.setText(oferta.getEmpresa().getNombre());
        lblEstado.setText(oferta.getEstado().toString());
        lblEstado.setStyle("-fx-background-color: " + CeldaEstado.colorFondo(oferta.getEstado()) + ";"
                + "-fx-text-fill: " + CeldaEstado.colorTexto(oferta.getEstado()) + ";"
                + "-fx-background-radius: 12; -fx-font-weight: bold; -fx-padding: 4 16;");
        lblFecha.setText(oferta.getFechaPostulacion().toString());
        lblSalario.setText(String.format(Locale.of("es", "ES"), "%,.0f €", oferta.getSalario()));
        lblEnlace.setText(oferta.getEnlace());
        lblDescripcion.setText(oferta.getDescripcion());

        colFechaEntrevista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colResultado.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getResultado()));
        colResultado.setCellFactory(columna -> new CeldaResultado());
        colNotasEntrevista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotas()));
        colAccionesEntrevista.setCellFactory(columna -> new CeldaAcciones<>(this::editarEntrevista, this::eliminarEntrevista));

        colFechaNota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
        colTextoNota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTexto()));
        colAccionesNota.setCellFactory(columna -> new CeldaAcciones<>("/images/icon-eliminar.png", this::eliminarNota));

        cargarNotas();
        cargarEntrevistas();
    }

    /**
     * Vuelve al listado de candidaturas.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void volver(ActionEvent actionEvent) {
        SceneNavigator.navigateToContenido("candidaturas-view.fxml");
    }

    /**
     * Guarda la nota escrita y recarga la tabla de notas.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void agregarNota(ActionEvent actionEvent) {
        try {
            servicioNota.agregar(oferta, txtNuevaNota.getText());
            cargarNotas();
            txtNuevaNota.clear();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Abre el formulario para agregar una entrevista y recarga el historial al cerrarlo.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void nuevaEntrevista(ActionEvent actionEvent) {
        AppContext.getInstance().getDatos().remove("entrevista");
        SceneNavigator.abrirModal("formulario-entrevista-view.fxml", "Nueva entrevista");
        cargarEntrevistas();
    }

    private void editarEntrevista(ActionEvent actionEvent) {
        AppContext.getInstance().put("entrevista", tablaEntrevistas.getSelectionModel().getSelectedItem());
        SceneNavigator.abrirModal("formulario-entrevista-view.fxml", "Editar entrevista");
        cargarEntrevistas();
    }

    private void eliminarEntrevista(ActionEvent actionEvent) {
        Entrevista entrevista = tablaEntrevistas.getSelectionModel().getSelectedItem();

        if (confirmarBorrado("¿Seguro que desea eliminar la " + entrevista.getTipo() + " del " + entrevista.getFecha() + "?")) {
            servicioEntrevista.borrar(entrevista);
            cargarEntrevistas();
        }
    }

    private void eliminarNota(ActionEvent actionEvent) {
        Nota nota = tablaNotas.getSelectionModel().getSelectedItem();

        if (confirmarBorrado("¿Seguro que desea eliminar la nota del " + nota.getFecha() + "?")) {
            servicioNota.borrar(nota);
            cargarNotas();
        }
    }

    private boolean confirmarBorrado(String mensaje) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar borrado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(mensaje);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private void cargarNotas() {
        tablaNotas.getItems().setAll(servicioNota.listarPorOferta(oferta));
    }

    private void cargarEntrevistas() {
        tablaEntrevistas.getItems().setAll(servicioEntrevista.listarPorOferta(oferta));
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Datos no válidos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

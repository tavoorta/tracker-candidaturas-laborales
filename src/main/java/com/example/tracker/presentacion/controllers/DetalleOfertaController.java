package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import com.example.tracker.common.AppContext;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioEntrevista;
import com.example.tracker.negocio.ServicioNota;
import com.example.tracker.presentacion.celdas.CeldaEstado;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Detalle de la candidatura guardada en el {@link AppContext} con la clave "oferta":
 * datos de la oferta, notas e historial de entrevistas.
 */
public class DetalleOfertaController implements Initializable {

    private final ServicioEntrevista servicioEntrevista;
    private final ServicioNota servicioNota;

    private Oferta oferta;

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
    private Label lblNotas;
    @javafx.fxml.FXML
    private TableView<Entrevista> tablaEntrevistas;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colFechaEntrevista;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colTipo;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colNotasEntrevista;
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
        colNotasEntrevista.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotas()));
        cargarNotas();
        cargarEntrevistas();
    }

    @javafx.fxml.FXML
    public void volver(ActionEvent actionEvent) {
        SceneNavigator.navigateToContenido("candidaturas-view.fxml");
    }

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

    @javafx.fxml.FXML
    public void nuevaEntrevista(ActionEvent actionEvent) {
        SceneNavigator.abrirModal("formulario-entrevista-view.fxml", "Nueva entrevista");
        cargarEntrevistas();
    }

    private void cargarNotas() {
        String texto = "";
        for (Nota nota : servicioNota.listarPorOferta(oferta)) {
            texto += nota.getFecha() + "\n" + nota.getTexto() + "\n\n";
        }
        lblNotas.setText(texto);
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

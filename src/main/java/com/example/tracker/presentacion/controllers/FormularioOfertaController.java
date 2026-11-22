package com.example.tracker.presentacion.controllers;

import com.example.tracker.common.AppContext;
import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioEmpresa;
import com.example.tracker.negocio.ServicioOferta;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Formulario modal de alta y edición de candidaturas. Si en el {@link AppContext}
 * hay una "oferta", el formulario se rellena con sus datos y al guardar se actualiza;
 * si no, se crea una nueva.
 */
public class FormularioOfertaController implements Initializable {

    private final ServicioOferta servicioOferta;
    private final ServicioEmpresa servicioEmpresa;

    public FormularioOfertaController(ServicioOferta servicioOferta, ServicioEmpresa servicioEmpresa) {
        this.servicioOferta = servicioOferta;
        this.servicioEmpresa = servicioEmpresa;
    }

    //region FXML
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private TextField txtTitulo;
    @javafx.fxml.FXML
    private ComboBox<String> comboEmpresa;
    @javafx.fxml.FXML
    private ComboBox<EstadoOferta> comboEstado;
    @javafx.fxml.FXML
    private DatePicker dateFecha;
    @javafx.fxml.FXML
    private TextField txtEnlace;
    @javafx.fxml.FXML
    private TextField txtSalario;
    @javafx.fxml.FXML
    private TextArea txtDescripcion;
    @javafx.fxml.FXML
    private Button btnGuardar;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboEmpresa.getItems().addAll(servicioEmpresa.listarNombres());
        comboEstado.getItems().addAll(EstadoOferta.values());
        comboEstado.setValue(EstadoOferta.PENDIENTE);
        dateFecha.setValue(LocalDate.now());

        if (AppContext.getInstance().getDatos().containsKey("oferta")) {
            Oferta oferta = (Oferta) AppContext.getInstance().get("oferta");

            lblTitulo.setText("Editar candidatura");
            btnGuardar.setText("Guardar cambios");

            txtTitulo.setText(oferta.getTitulo());
            comboEmpresa.setValue(oferta.getEmpresa().getNombre());
            comboEstado.setValue(oferta.getEstado());
            dateFecha.setValue(oferta.getFechaPostulacion());
            txtEnlace.setText(oferta.getEnlace());
            txtSalario.setText(String.format(Locale.ROOT, "%.0f", oferta.getSalario()));
            txtDescripcion.setText(oferta.getDescripcion());
        }
    }

    @javafx.fxml.FXML
    public void guardar(ActionEvent actionEvent) {
        Oferta oferta = new Oferta();

        if (AppContext.getInstance().getDatos().containsKey("oferta")) {
            Oferta original = (Oferta) AppContext.getInstance().get("oferta");
            oferta.setOfertaId(original.getOfertaId());
        }

        oferta.setTitulo(txtTitulo.getText());
        oferta.setEmpresa(servicioEmpresa.buscarPorNombre(comboEmpresa.getValue()));
        oferta.setEstado(comboEstado.getValue());
        oferta.setFechaPostulacion(dateFecha.getValue());
        oferta.setEnlace(txtEnlace.getText());
        oferta.setDescripcion(txtDescripcion.getText());

        try {
            oferta.setSalario(txtSalario.getText().isBlank() ? 0 : Double.parseDouble(txtSalario.getText()));
            servicioOferta.guardar(oferta);
            cerrar();
        } catch (NumberFormatException e) {
            mostrarError("El salario debe ser un número, por ejemplo 24000.");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void cancelar(ActionEvent actionEvent) {
        cerrar();
    }

    private void cerrar() {
        Stage ventana = (Stage) txtTitulo.getScene().getWindow();
        ventana.close();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Datos no válidos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

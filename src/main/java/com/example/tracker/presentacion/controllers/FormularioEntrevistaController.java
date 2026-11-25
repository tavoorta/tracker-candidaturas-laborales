package com.example.tracker.presentacion.controllers;

import com.example.tracker.common.AppContext;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoEntrevista;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioEntrevista;
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
import java.util.ResourceBundle;

/**
 * Formulario modal de alta y edición de entrevistas de la candidatura guardada en el
 * {@link AppContext} con la clave "oferta". Si además hay una "entrevista", el
 * formulario se rellena con sus datos y al guardar se actualiza.
 */
public class FormularioEntrevistaController implements Initializable {

    private final ServicioEntrevista servicioEntrevista;

    private Oferta oferta;

    /**
     * @param servicioEntrevista servicio de entrevistas
     */
    public FormularioEntrevistaController(ServicioEntrevista servicioEntrevista) {
        this.servicioEntrevista = servicioEntrevista;
    }

    //region FXML
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private DatePicker dateFecha;
    @javafx.fxml.FXML
    private ComboBox<TipoEntrevista> comboTipo;
    @javafx.fxml.FXML
    private ComboBox<Estado> comboResultado;
    @javafx.fxml.FXML
    private TextField txtRol;
    @javafx.fxml.FXML
    private TextField txtEmpresa;
    @javafx.fxml.FXML
    private TextArea txtNotas;
    @javafx.fxml.FXML
    private Button btnGuardar;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        oferta = (Oferta) AppContext.getInstance().get("oferta");

        comboTipo.getItems().addAll(TipoEntrevista.values());
        comboResultado.getItems().addAll(Estado.values());
        comboResultado.setValue(Estado.PENDIENTE);
        dateFecha.setValue(LocalDate.now());
        txtRol.setText(oferta.getTitulo());
        txtEmpresa.setText(oferta.getEmpresa().getNombre());

        if (AppContext.getInstance().getDatos().containsKey("entrevista")) {
            Entrevista entrevista = (Entrevista) AppContext.getInstance().get("entrevista");

            lblTitulo.setText("Editar entrevista");
            btnGuardar.setText("Guardar cambios");

            dateFecha.setValue(entrevista.getFecha());
            comboTipo.setValue(entrevista.getTipo());
            comboResultado.setValue(entrevista.getResultado());
            txtNotas.setText(entrevista.getNotas());
        }
    }

    /**
     * Guarda los datos del formulario y cierra la ventana. Si algún dato no es válido,
     * muestra el error y deja la ventana abierta.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void guardar(ActionEvent actionEvent) {
        Entrevista entrevista = new Entrevista();

        if (AppContext.getInstance().getDatos().containsKey("entrevista")) {
            Entrevista original = (Entrevista) AppContext.getInstance().get("entrevista");
            entrevista.setEntrevistaId(original.getEntrevistaId());
        }

        entrevista.setOferta(oferta);
        entrevista.setFecha(dateFecha.getValue());
        entrevista.setTipo(comboTipo.getValue());
        entrevista.setResultado(comboResultado.getValue());
        entrevista.setNotas(txtNotas.getText());

        try {
            servicioEntrevista.guardar(entrevista);
            cerrar();
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Datos no válidos");
            alerta.setHeaderText(null);
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }

    /**
     * Cierra el formulario sin guardar.
     *
     * @param actionEvent evento del botón
     */
    @javafx.fxml.FXML
    public void cancelar(ActionEvent actionEvent) {
        cerrar();
    }

    private void cerrar() {
        Stage ventana = (Stage) txtRol.getScene().getWindow();
        ventana.close();
    }
}

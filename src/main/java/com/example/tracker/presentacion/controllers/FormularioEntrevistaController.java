package com.example.tracker.presentacion.controllers;

import com.example.tracker.common.AppContext;
import com.example.tracker.datos.enums.TipoEntrevista;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioEntrevista;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Formulario modal de alta de entrevistas de la candidatura guardada en el
 * {@link AppContext} con la clave "oferta".
 */
public class FormularioEntrevistaController implements Initializable {

    private final ServicioEntrevista servicioEntrevista;

    private Oferta oferta;

    public FormularioEntrevistaController(ServicioEntrevista servicioEntrevista) {
        this.servicioEntrevista = servicioEntrevista;
    }

    //region FXML
    @javafx.fxml.FXML
    private DatePicker dateFecha;
    @javafx.fxml.FXML
    private ComboBox<TipoEntrevista> comboTipo;
    @javafx.fxml.FXML
    private TextField txtRol;
    @javafx.fxml.FXML
    private TextField txtEmpresa;
    @javafx.fxml.FXML
    private TextArea txtNotas;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        oferta = (Oferta) AppContext.getInstance().get("oferta");

        comboTipo.getItems().addAll(TipoEntrevista.values());
        dateFecha.setValue(LocalDate.now());
        txtRol.setText(oferta.getTitulo());
        txtEmpresa.setText(oferta.getEmpresa().getNombre());
    }

    @javafx.fxml.FXML
    public void guardar(ActionEvent actionEvent) {
        Entrevista entrevista = new Entrevista();
        entrevista.setOferta(oferta);
        entrevista.setFecha(dateFecha.getValue());
        entrevista.setTipo(comboTipo.getValue());
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

    @javafx.fxml.FXML
    public void cancelar(ActionEvent actionEvent) {
        cerrar();
    }

    private void cerrar() {
        Stage ventana = (Stage) txtRol.getScene().getWindow();
        ventana.close();
    }
}

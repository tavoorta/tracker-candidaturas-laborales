package com.example.tracker.presentacion.controllers;

import com.example.tracker.common.AppContext;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.negocio.ServicioEmpresa;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Formulario modal de alta y edición de empresas. Si en el {@link AppContext}
 * hay una "empresa", el formulario se rellena con sus datos y al guardar se actualiza;
 * si no, se crea una nueva.
 */
public class FormularioEmpresaController implements Initializable {

    private final ServicioEmpresa servicioEmpresa;

    public FormularioEmpresaController(ServicioEmpresa servicioEmpresa) {
        this.servicioEmpresa = servicioEmpresa;
    }

    //region FXML
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private TextField txtNombre;
    @javafx.fxml.FXML
    private TextField txtUbicacion;
    @javafx.fxml.FXML
    private TextField txtSector;
    @javafx.fxml.FXML
    private TextField txtWeb;
    @javafx.fxml.FXML
    private TextField txtLinkedIn;
    @javafx.fxml.FXML
    private TextArea txtNotas;
    @javafx.fxml.FXML
    private Button btnGuardar;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (AppContext.getInstance().getDatos().containsKey("empresa")) {
            Empresa empresa = (Empresa) AppContext.getInstance().get("empresa");

            lblTitulo.setText("Editar empresa");
            btnGuardar.setText("Guardar cambios");

            txtNombre.setText(empresa.getNombre());
            txtUbicacion.setText(empresa.getUbicacion());
            txtSector.setText(empresa.getSector());
            txtWeb.setText(empresa.getWeb());
            txtLinkedIn.setText(empresa.getLinkedIn());
            txtNotas.setText(empresa.getNotas());
        }
    }

    @javafx.fxml.FXML
    public void guardar(ActionEvent actionEvent) {
        Empresa empresa = new Empresa();

        if (AppContext.getInstance().getDatos().containsKey("empresa")) {
            Empresa original = (Empresa) AppContext.getInstance().get("empresa");
            empresa.setEmpresaId(original.getEmpresaId());
            empresa.setFechaRegistro(original.getFechaRegistro());
        }

        empresa.setNombre(txtNombre.getText());
        empresa.setUbicacion(txtUbicacion.getText());
        empresa.setSector(txtSector.getText());
        empresa.setWeb(txtWeb.getText());
        empresa.setLinkedIn(txtLinkedIn.getText());
        empresa.setNotas(txtNotas.getText());

        try {
            servicioEmpresa.guardar(empresa);
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
        Stage ventana = (Stage) txtNombre.getScene().getWindow();
        ventana.close();
    }
}

package com.example.tracker.presentacion.controllers;

import com.example.tracker.SceneNavigator;
import com.example.tracker.common.AppContext;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Actividad;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import com.example.tracker.negocio.ServicioActividad;
import com.example.tracker.negocio.ServicioEntrevista;
import com.example.tracker.negocio.ServicioOferta;
import com.example.tracker.presentacion.celdas.CeldaAcciones;
import com.example.tracker.presentacion.celdas.CeldaActividad;
import com.example.tracker.presentacion.celdas.CeldaEstado;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Pantalla de inicio: totales con su variación semanal, próximas entrevistas,
 * actividad reciente y últimas candidaturas.
 */
public class DashboardController implements Initializable {

    private static final int FILAS_POR_TABLA = 5;

    private final ServicioOferta servicioOferta;
    private final ServicioEntrevista servicioEntrevista;
    private final ServicioActividad servicioActividad;

    public DashboardController(ServicioOferta servicioOferta, ServicioEntrevista servicioEntrevista,
                               ServicioActividad servicioActividad) {
        this.servicioOferta = servicioOferta;
        this.servicioEntrevista = servicioEntrevista;
        this.servicioActividad = servicioActividad;
    }

    //region FXML
    @javafx.fxml.FXML
    private Label lblOfertas;
    @javafx.fxml.FXML
    private Label lblTituloOfertas;
    @javafx.fxml.FXML
    private Label lblVariacionOfertas;
    @javafx.fxml.FXML
    private Label lblEntrevistas;
    @javafx.fxml.FXML
    private Label lblTituloEntrevistas;
    @javafx.fxml.FXML
    private Label lblVariacionEntrevistas;
    @javafx.fxml.FXML
    private Label lblPendientes;
    @javafx.fxml.FXML
    private Label lblTituloPendientes;
    @javafx.fxml.FXML
    private Label lblVariacionPendientes;
    @javafx.fxml.FXML
    private Label lblRechazos;
    @javafx.fxml.FXML
    private Label lblTituloRechazos;
    @javafx.fxml.FXML
    private Label lblVariacionRechazos;
    @javafx.fxml.FXML
    private TableView<Entrevista> tablaProximasEntrevistas;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colEntrevistaEmpresa;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colEntrevistaTitulo;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, String> colEntrevistaFecha;
    @javafx.fxml.FXML
    private TableColumn<Entrevista, Void> colEntrevistaInfo;
    @javafx.fxml.FXML
    private TableView<Actividad> tablaActividad;
    @javafx.fxml.FXML
    private TableColumn<Actividad, Actividad> colActividadIcono;
    @javafx.fxml.FXML
    private TableColumn<Actividad, String> colActividadDescripcion;
    @javafx.fxml.FXML
    private TableView<Oferta> tablaUltimasCandidaturas;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colUltimaEmpresa;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colUltimaTitulo;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colUltimaFecha;
    @javafx.fxml.FXML
    private TableColumn<Oferta, Estado> colUltimoEstado;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colUltimoSalario;
    @javafx.fxml.FXML
    private TableColumn<Oferta, Void> colUltimasAcciones;
    //endregion FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colEntrevistaEmpresa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOferta().getEmpresa().getNombre()));
        colEntrevistaTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOferta().getTitulo()));
        colEntrevistaFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
        colEntrevistaInfo.setCellFactory(columna -> new CeldaAcciones<>("/images/icon-info.png", this::verOfertaDeEntrevista));

        colActividadIcono.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        colActividadIcono.setCellFactory(columna -> new CeldaActividad());
        colActividadDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        colUltimaEmpresa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmpresa().getNombre()));
        colUltimaTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colUltimaFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaPostulacion().toString()));
        colUltimoEstado.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEstado()));
        colUltimoEstado.setCellFactory(columna -> new CeldaEstado(this::cambiarEstado));
        colUltimoSalario.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.of("es", "ES"), "%,.0f €", c.getValue().getSalario())));
        colUltimasAcciones.setCellFactory(columna -> new CeldaAcciones<>("/images/icon-info.png", this::verUltimaCandidatura));

        cargarDatos();
    }

    private void cargarDatos() {
        aplicarKpi(lblOfertas, lblTituloOfertas, lblVariacionOfertas, "Ofertas", null);
        aplicarKpi(lblEntrevistas, lblTituloEntrevistas, lblVariacionEntrevistas, "Entrevistas", Estado.ENTREVISTA);
        aplicarKpi(lblPendientes, lblTituloPendientes, lblVariacionPendientes, "Pendientes", Estado.PENDIENTE);
        aplicarKpi(lblRechazos, lblTituloRechazos, lblVariacionRechazos, "Rechazos", Estado.RECHAZADO);

        tablaProximasEntrevistas.getItems().setAll(servicioEntrevista.listarProximas(FILAS_POR_TABLA));
        tablaActividad.getItems().setAll(servicioActividad.listarRecientes(FILAS_POR_TABLA));
        tablaUltimasCandidaturas.getItems().setAll(servicioOferta.listarUltimas(FILAS_POR_TABLA));
    }

    /** Rellena una tarjeta con el total del estado y su variación respecto a la semana anterior. */
    private void aplicarKpi(Label lblValor, Label lblTitulo, Label lblVariacion, String titulo, Estado estado) {
        lblValor.setText(String.valueOf(servicioOferta.contar(estado, null)));
        lblTitulo.setText(titulo);

        int variacion = servicioOferta.variacionSemanal(estado);
        boolean subio = variacion >= 0;
        lblVariacion.setText(Math.abs(variacion) + "% " + (subio ? "más" : "menos") + " que la semana anterior");
        lblVariacion.setStyle("-fx-text-fill: " + (subio ? "#16A34A" : "#DC2626") + "; -fx-font-size: 11;");
    }

    private void verOfertaDeEntrevista(ActionEvent actionEvent) {
        Entrevista entrevista = tablaProximasEntrevistas.getSelectionModel().getSelectedItem();
        abrirDetalle(servicioOferta.buscarPorId(entrevista.getOferta().getOfertaId()));
    }

    private void verUltimaCandidatura(ActionEvent actionEvent) {
        abrirDetalle(tablaUltimasCandidaturas.getSelectionModel().getSelectedItem());
    }

    private void abrirDetalle(Oferta oferta) {
        AppContext.getInstance().put("oferta", oferta);
        SceneNavigator.navigateToContenido("detalle-oferta-view.fxml");
    }

    private void cambiarEstado(ActionEvent actionEvent) {
        Oferta oferta = tablaUltimasCandidaturas.getSelectionModel().getSelectedItem();
        ComboBox<Estado> comboDeLaFila = (ComboBox<Estado>) actionEvent.getSource();

        try {
            servicioOferta.cambiarEstado(oferta, comboDeLaFila.getValue());
        } catch (RuntimeException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("No se pudo cambiar el estado");
            alerta.setHeaderText(null);
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
        cargarDatos();
    }
}

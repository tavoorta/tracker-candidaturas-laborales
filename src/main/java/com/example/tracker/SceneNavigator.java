package com.example.tracker;

import com.example.tracker.common.AppContext;
import com.example.tracker.presentacion.controllers.CandidaturasController;
import com.example.tracker.presentacion.controllers.DashboardController;
import com.example.tracker.presentacion.controllers.DetalleOfertaController;
import com.example.tracker.presentacion.controllers.EmpresasController;
import com.example.tracker.presentacion.controllers.FormularioEmpresaController;
import com.example.tracker.presentacion.controllers.FormularioEntrevistaController;
import com.example.tracker.presentacion.controllers.FormularioOfertaController;
import com.example.tracker.presentacion.controllers.LoginController;
import com.example.tracker.presentacion.controllers.ShellController;
import com.example.tracker.negocio.ServicioActividad;
import com.example.tracker.negocio.ServicioEmpresa;
import com.example.tracker.negocio.ServicioEntrevista;
import com.example.tracker.negocio.ServicioNota;
import com.example.tracker.negocio.ServicioOferta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    private static Stage stage;
    private static StackPane contenido;

    private static ServicioOferta servicioOferta;
    private static ServicioEmpresa servicioEmpresa;
    private static ServicioEntrevista servicioEntrevista;
    private static ServicioNota servicioNota;
    private static ServicioActividad servicioActividad;

    public static void setStage(Stage stage) {
        SceneNavigator.stage = stage;
    }

    public static void setContenido(StackPane contenido) {
        SceneNavigator.contenido = contenido;
    }

    public static void setServicioOferta(ServicioOferta servicioOferta) {
        SceneNavigator.servicioOferta = servicioOferta;
    }

    public static void setServicioEmpresa(ServicioEmpresa servicioEmpresa) {
        SceneNavigator.servicioEmpresa = servicioEmpresa;
    }

    public static void setServicioEntrevista(ServicioEntrevista servicioEntrevista) {
        SceneNavigator.servicioEntrevista = servicioEntrevista;
    }

    public static void setServicioNota(ServicioNota servicioNota) {
        SceneNavigator.servicioNota = servicioNota;
    }

    public static void setServicioActividad(ServicioActividad servicioActividad) {
        SceneNavigator.servicioActividad = servicioActividad;
    }

    /** Utilizado para navegar a una vista específica. */
    public static void navigateToInjection(String fxml, String title) {

        FXMLLoader fxmlLoader = crearLoader(fxml);

        try {
            stage.setTitle(title);
            if (stage.getScene() == null) {
                stage.setScene(new Scene(fxmlLoader.load(), 1200, 720));
            } else {
                stage.getScene().setRoot(fxmlLoader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Utilizado para rellenar el contenido del shell. */
    public static void navigateToContenido(String fxml) {

        FXMLLoader fxmlLoader = crearLoader(fxml);

        try {
            contenido.getChildren().clear();
            contenido.getChildren().add(fxmlLoader.load());
            AppContext.getInstance().put("vistaActual", fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Abre la vista en una ventana modal y espera a que se cierre. */
    public static void abrirModal(String fxml, String title) {

        FXMLLoader fxmlLoader = crearLoader(fxml);

        try {
            Stage modal = new Stage();
            modal.initOwner(stage);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(title);
            modal.setScene(new Scene(fxmlLoader.load()));
            modal.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** utilizado para crear un FXMLLoader con la ruta y el controllerFactory que crea cada controlador con su servicio. */
    private static FXMLLoader crearLoader(String fxml) {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/" + fxml));

        fxmlLoader.setControllerFactory(type -> {
            return switch (fxml) {
                case "login-view.fxml" -> new LoginController();
                case "shell-view.fxml" -> new ShellController();
                case "dashboard-view.fxml" -> new DashboardController(servicioOferta, servicioEntrevista, servicioActividad);
                case "candidaturas-view.fxml" -> new CandidaturasController(servicioOferta);
                case "formulario-oferta-view.fxml" -> new FormularioOfertaController(servicioOferta, servicioEmpresa);
                case "detalle-oferta-view.fxml" -> new DetalleOfertaController(servicioEntrevista, servicioNota);
                case "formulario-entrevista-view.fxml" -> new FormularioEntrevistaController(servicioEntrevista);
                case "empresas-view.fxml" -> new EmpresasController(servicioEmpresa);
                case "formulario-empresa-view.fxml" -> new FormularioEmpresaController(servicioEmpresa);
                default -> throw new IllegalArgumentException("Error con fxml: " + fxml);
            };
        });
        return fxmlLoader;
    }
}
